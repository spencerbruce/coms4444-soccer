package sim;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Simulator {
	
	private static GameHistory gameHistory;
	private static List<PlayerWrapper> playerWrappers;
	private static List<String> playerNames;
	private static Integer[][] randomGameGrid;
    private static Random random;
	
	// Constants
	private static int seed = 42;
	private static int rounds = 10;
	private static double fps = 30;
	
    private static int currentRound = 0;
    private static long timeout = 1000;
    private static boolean showGUI = false;
    private static boolean continuousGUI = true;
    private static String version = "1.0";
	private static String projectPath, sourcePath, staticsPath;
    	
	
	private static void setup() {
		gameHistory = new GameHistory();
		random = new Random(seed);		
		projectPath = new File(".").getAbsolutePath().substring(0, 
				new File(".").getAbsolutePath().indexOf("coms4444-soccer") + "coms4444-soccer".length());
		sourcePath = projectPath + File.separator + "src";
		staticsPath = projectPath + File.separator + "statics";
	}

	private static void parseCommandLineArguments(String[] args) {
		playerWrappers = new ArrayList<>();
		playerNames = new ArrayList<>();

		for(int i = 0; i < args.length; i++) {
            switch (args[i].charAt(0)) {
                case '-':
                    if(args[i].equals("-p") || args[i].equals("--players")) {
                        while(i + 1 < args.length && args[i + 1].charAt(0) != '-') {
                            i++;
                            String playerName = args[i];
                            playerNames.add(playerName);
                            try {
								playerWrappers.add(loadPlayerWrapper(cleanName(playerName), playerName));
							} catch (Exception e) {
								e.printStackTrace();
							}
                        }

                        if(playerNames.size() < 2) 
                            throw new IllegalArgumentException("You entered an invalid number of teams. At least 2 teams are required for the league.");
                    } 
                    else if(args[i].equals("-g") || args[i].equals("--gui"))
                        showGUI = true;
                    else if(args[i].equals("-c") || args[i].equals("--continuous"))
                        continuousGUI = true;
                    else if(args[i].equals("-d") || args[i].equals("--discrete"))
                        continuousGUI = false;
                    else if(args[i].equals("-l") || args[i].equals("--log")) {
                        i++;
                    	if(i == args.length) 
                            throw new IllegalArgumentException("The log file name is missing!");
                        Log.setLogFile(args[i]);
                        Log.assignLoggingStatus(true);
                    }
                    else if(args[i].equals("-v") || args[i].equals("--verbose"))
                        Log.assignVerbosityStatus(true);
                    else if(args[i].equals("-f") || args[i].equals("--fps")) {
                    	i++;
                        if(i == args.length) 
                            throw new IllegalArgumentException("The GUI frames per second is missing!");
                        fps = Double.parseDouble(args[i]);
                    }
                    else if(args[i].equals("-s") || args[i].equals("--seed")) {
                    	i++;
                        if(i == args.length) 
                            throw new IllegalArgumentException("The seed number is missing!");
                        seed = Integer.parseInt(args[i]);
                    }
                    else if(args[i].equals("-r") || args[i].equals("--rounds")) {
                    	i++;
                        if (i == args.length) 
                            throw new IllegalArgumentException("The total number of rounds is not specified!");
                        rounds = Integer.parseInt(args[i]);
                    }
                    else 
                        throw new IllegalArgumentException("Unknown argument \"" + args[i] + "\"!");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument \"" + args[i] + "\"!");
            }
        }

        Log.writeToLogFile("Project: Retroactive Soccer");
        Log.writeToLogFile("Simulator Version: " + version);
        Log.writeToLogFile("Players: " + playerNames.toString());
        Log.writeToLogFile("GUI: " + (showGUI ? "enabled" : "disabled"));
	}

	private static void runSimulation() {
		for(int i = 0; i <= rounds; i++) {
			System.out.println("Round: " + i);
			currentRound = i;
			Map<Integer, List<Game>> roundGamesMap = new HashMap<>();
			
			if(currentRound == 0) {   // Round 0: simulator game randomization
				generateRandomGameGrid();
				for(PlayerWrapper playerWrapper : playerWrappers) {
					int playerTeamID = playerWrapper.getPlayer().getID();
					List<Game> randomPlayerGames = assignGamesToPlayer(playerWrapper);
					roundGamesMap.put(playerTeamID, randomPlayerGames);
				}
				gameHistory.addRoundGames(currentRound, roundGamesMap);
			}
			else {   // Reallocation rounds
				for(PlayerWrapper playerWrapper : playerWrappers) {
					int playerTeamID = playerWrapper.getPlayer().getID();
					int previousRound = currentRound - 1;
					List<Game> playerGames = gameHistory.getAllGamesMap().get(previousRound).get(playerTeamID);

					Map<Integer, List<Game>> opponentGamesMap = new HashMap<>();
					for(PlayerWrapper opponentPlayerWrapper : playerWrappers) {
						int opponentTeamID = opponentPlayerWrapper.getPlayer().getID();
						if(playerTeamID == opponentTeamID)
							continue;
						List<Game> opponentGames = gameHistory.getAllGamesMap().get(previousRound).get(opponentTeamID);
						opponentGamesMap.put(opponentTeamID, opponentGames);
					}
					
					List<Game> reallocatedPlayerGames = 
							playerWrapper.reallocate(currentRound, deepClone(gameHistory), deepClone(playerGames), deepClone(opponentGamesMap));

					if(!Player.checkConstraintsSatisfied(playerGames, reallocatedPlayerGames))
						roundGamesMap.put(playerTeamID, deepClone(playerGames));
					else
						roundGamesMap.put(playerTeamID, deepClone(reallocatedPlayerGames));
				}
				
				Map<Integer, PlayerPoints> roundPointsMap = computeTeamPoints(roundGamesMap);
				Map<Integer, PlayerPoints> roundCumulativePointsMap = computeCumulativeTeamPoints(roundPointsMap);
				Map<Integer, Double> roundRankingsMap = computeRankings(roundPointsMap);
				Map<Integer, Double> roundAverageRankingsMap = computeRankings(roundCumulativePointsMap);
				updateGameHistory(currentRound, roundGamesMap, roundPointsMap, roundCumulativePointsMap, roundRankingsMap, roundAverageRankingsMap);	
			}
		}
	}

	private static Map<Integer, PlayerPoints> computeTeamPoints(Map<Integer, List<Game>> roundGamesMap) {
		Map<Integer, PlayerPoints> roundPointsMap = new HashMap<>();
		for(Map.Entry<Integer, List<Game>> gamesEntry : roundGamesMap.entrySet()) {
			PlayerPoints playerPoints = new PlayerPoints();
			for(Game game : gamesEntry.getValue()) {
				if(Player.hasWonGame(game))
					playerPoints.addWinPoints();
				else if(Player.hasLostGame(game))
					playerPoints.addLossPoints();
				else if(Player.hasDrawnGame(game))
					playerPoints.addDrawPoints();
			}
			roundPointsMap.put(gamesEntry.getKey(), playerPoints);
		}
		return roundPointsMap;
	}

	private static Map<Integer, PlayerPoints> computeCumulativeTeamPoints(Map<Integer, PlayerPoints> roundPointsMap) {
		Map<Integer, PlayerPoints> roundCumulativePointsMap = new HashMap<>();
		
		Map<Integer, Map<Integer, PlayerPoints>> allCumulativePointsMap = gameHistory.getAllCumulativePointsMap();
		for(Map.Entry<Integer, PlayerPoints> newPointsEntry : roundPointsMap.entrySet()) {
			int roundCumulativePoints = newPointsEntry.getValue().getTotalPoints();
			for(Integer round : allCumulativePointsMap.keySet())
				roundCumulativePoints += allCumulativePointsMap.get(round).get(newPointsEntry.getKey()).getTotalPoints();
			roundCumulativePointsMap.put(newPointsEntry.getKey(), new PlayerPoints(roundCumulativePoints));
		}
		
		return roundCumulativePointsMap;
	}
	
	private static Map<Integer, Double> computeRankings(Map<Integer, PlayerPoints> pointsMap) {
		Map<Integer, Double> rankingsMap = new HashMap<>();
		
		Map<Integer, PlayerPoints> rankedPointsMap = pointsMap.entrySet()
				  .stream()
				  .sorted(Map.Entry.comparingByValue())
				  .collect(Collectors.toMap(
				    Map.Entry::getKey, 
				    Map.Entry::getValue,
				    (oldPoints, newPoints) -> oldPoints, LinkedHashMap::new));
						
		List<Integer> rankedTeamIDs = new ArrayList<>(rankedPointsMap.keySet());
		Collections.reverse(rankedTeamIDs);
				
		int indexOfFirstTiedElement = 0;
		for(int i = 0; i < rankedTeamIDs.size() - 1; i++) {
			int currTeamID = rankedTeamIDs.get(i);
			int currTeamNumPoints = rankedPointsMap.get(currTeamID).getTotalPoints();
			int nextTeamID = rankedTeamIDs.get(i + 1);
			int nextTeamNumPoints = rankedPointsMap.get(nextTeamID).getTotalPoints();
			
			if(currTeamNumPoints == nextTeamNumPoints)
				continue;
			else if(indexOfFirstTiedElement != i) {
				double averageRank = ((double) (indexOfFirstTiedElement + i)) / 2 + 1;
				for(int j = indexOfFirstTiedElement; j <= i; j++)
					rankingsMap.put(rankedTeamIDs.get(j), averageRank);
				indexOfFirstTiedElement = i + 1;
			}
			else {
				rankingsMap.put(currTeamID, (double) (i + 1));
				indexOfFirstTiedElement = i + 1;
			}
		}
		
		if(indexOfFirstTiedElement != rankedTeamIDs.size() - 1) {
			double averageRank = ((double) (indexOfFirstTiedElement + rankedTeamIDs.size() - 1)) / 2 + 1;
			for(int i = indexOfFirstTiedElement; i < rankedTeamIDs.size(); i++)
				rankingsMap.put(rankedTeamIDs.get(i), averageRank);
		}
		else
			rankingsMap.put(rankedTeamIDs.get(rankedTeamIDs.size() - 1), (double) rankedTeamIDs.size());

		return rankingsMap;
	}	

	private static void updateGameHistory(Integer round,
										  Map<Integer, List<Game>> roundGamesMap,
										  Map<Integer, PlayerPoints> roundPointsMap,
										  Map<Integer, PlayerPoints> roundCumulativePointsMap,
										  Map<Integer, Double> roundRankingsMap,
										  Map<Integer, Double> roundAverageRankingsMap) {
		gameHistory.addRoundGames(round, roundGamesMap);
		gameHistory.addRoundPoints(round, roundPointsMap);
		gameHistory.addRoundCumulativePoints(round, roundCumulativePointsMap);
		gameHistory.addRoundRankings(round, roundRankingsMap);
		gameHistory.addRoundAverageRankings(round, roundAverageRankingsMap);
	}
	
	private static String cleanName(String playerName) {
       String cleanedPlayerName = " ";
       if(playerName.contains("_")) {
           Integer index = playerName.lastIndexOf("_");
           cleanedPlayerName = playerName.substring(0, index);
       }
       else
           return playerName;

       return cleanedPlayerName;
    }
	
	private static void generateRandomGameGrid() {
		randomGameGrid = new Integer[playerWrappers.size()][playerWrappers.size()];
		for(int i = 0; i < randomGameGrid.length; i++)
			for(int j = 0; j < randomGameGrid[i].length; j++) {
				if(i == j)
					randomGameGrid[i][j] = 0;
				else
					randomGameGrid[i][j] = random.nextInt(8) + 1;
			}
	}
	
	
	private static List<Game> assignGamesToPlayer(PlayerWrapper playerWrapper) {
		List<Game> playerGames = new ArrayList<>();
		
		int indexOfPlayerWrapper = playerWrappers.indexOf(playerWrapper);
		for(int i = 0; i < randomGameGrid[indexOfPlayerWrapper].length; i++) {
			if(i == indexOfPlayerWrapper)
				continue;

			int gameID = i + 1;
			int numPlayerGoals = randomGameGrid[indexOfPlayerWrapper][i];
			int numOpponentGoals = randomGameGrid[i][indexOfPlayerWrapper];
			
			Game game = new Game(gameID, new Score(numPlayerGoals, numOpponentGoals));
			playerGames.add(game);
		}
		
		return playerGames;
	}
	
	private static <T extends Object> T deepClone(T obj) {
        if(obj == null)
            return null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(bais);
            
            return (T) objectInputStream.readObject();
        }
        catch(Exception e) {
            return null;
        }
	}
	
	private static PlayerWrapper loadPlayerWrapper(String playerName, String modifiedPlayerName) throws Exception {
		Log.writeToLogFile("Loading player " + playerName + "...");

		int teamID = playerWrappers.size() + 1;
		Player player = loadPlayer(playerName, teamID);
        if(player == null) {
            Log.writeToLogFile("Cannot load player " + playerName + "!");
            System.exit(1);
        }

        return new PlayerWrapper(player, modifiedPlayerName, timeout);
    }
	
	private static Player loadPlayer(String playerName, int teamID) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String playerPackagePath = sourcePath + File.separator + playerName;
        Set<File> playerFiles = getFilesInDirectory(playerPackagePath, ".java");
		String simPath = sourcePath + File.separator + "sim";
        Set<File> simFiles = getFilesInDirectory(simPath, ".java");

        File classFile = new File(playerPackagePath + File.separator + "Player.class");

        long classModified = classFile.exists() ? classFile.lastModified() : -1;
        if(classModified < 0 || classModified < lastModified(playerFiles) || classModified < lastModified(simFiles)) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if(compiler == null)
                throw new IOException("Cannot find the Java compiler!");

            StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
            Log.writeToLogFile("Compiling for team " + playerName + "...");

            if(!compiler.getTask(null, manager, null, null, null, manager.getJavaFileObjectsFromFiles(playerFiles)).call())
                throw new IOException("The compilation failed!");
            
            classFile = new File(playerPackagePath + File.separator + "Player.class");
            if(!classFile.exists())
                throw new FileNotFoundException("The class file is missing!");
        }

        ClassLoader loader = Simulator.class.getClassLoader();
        if(loader == null)
            throw new IOException("Cannot find the Java class loader!");

        @SuppressWarnings("rawtypes")
        Class rawClass = loader.loadClass(playerName + ".Player");
        Class[] classArgs = new Class[]{Integer.class, Integer.class, Integer.class};

        return (Player) rawClass.getDeclaredConstructor(classArgs).newInstance(teamID, rounds, seed);
    }

	private static long lastModified(Iterable<File> files) {
        long lastDate = 0;
        for(File file : files) {
            long date = file.lastModified();
            if(lastDate < date)
                lastDate = date;
        }
        return lastDate;
    }
	
	private static Set<File> getFilesInDirectory(String path, String extension) {
		Set<File> files = new HashSet<File>();
        Set<File> previousDirectories = new HashSet<File>();
        previousDirectories.add(new File(path));
        do {
        	Set<File> nextDirectories = new HashSet<File>();
            for(File previousDirectory : previousDirectories)
                for(File file : previousDirectory.listFiles()) {
                    if(!file.canRead())
                    	continue;
                    
                    if(file.isDirectory())
                        nextDirectories.add(file);
                    else if(file.getPath().endsWith(extension))
                        files.add(file);
                }
            previousDirectories = nextDirectories;
        } while(!previousDirectories.isEmpty());
        
        return files;
	}
	
	private static void updateGUI(HTTPServer server, String content) {
		if(server == null)
			return;
		
        String guiPath = null;
        while(true) {
            while(true) {
                try {
                	guiPath = server.request();
                    break;
                } catch(IOException e) {
                    Log.writeToVerboseLogFile("HTTP request error: " + e.getMessage());
                }
            }
            
            if(guiPath.equals("data.txt")) {
                try {
                    server.reply(content);
                } catch(IOException e) {
                    Log.writeToVerboseLogFile("HTTP dynamic reply error: " + e.getMessage());
                }
                return;
            }
            
            if(guiPath.equals(""))
            	guiPath = "webpage.html";
            else if(!Character.isLetter(guiPath.charAt(0))) {
                Log.writeToVerboseLogFile("Potentially malicious HTTP request: \"" + guiPath + "\"");
                break;
            }

            try {
                File file = new File(staticsPath + File.separator + guiPath);
                server.reply(file);
            } catch(IOException e) {
                Log.writeToVerboseLogFile("HTTP static reply error: " + e.getMessage());
            }
        }		
	}
	
	private static String getGUIState(double fps) {
		return "";
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		setup();
		parseCommandLineArguments(args);
		runSimulation();
		System.out.println(gameHistory);
		
//		List<PlayerPoints> playerPointsList = new ArrayList<>();
//		playerPointsList.add(new PlayerPoints(10));
//		playerPointsList.add(new PlayerPoints(23));
//		playerPointsList.add(new PlayerPoints(10));
//		playerPointsList.add(new PlayerPoints(135));
//		playerPointsList.add(new PlayerPoints(10));
//		playerPointsList.add(new PlayerPoints(10));
//		playerPointsList.add(new PlayerPoints(45));
//		
//		Map<Integer, PlayerPoints> map = new HashMap<>();
//		map.put(1, playerPointsList.get(0));
//		map.put(2, playerPointsList.get(1));
//		map.put(3, playerPointsList.get(2));
//		map.put(4, playerPointsList.get(3));
//		map.put(5, playerPointsList.get(4));
//		map.put(6, playerPointsList.get(5));
//		map.put(7, playerPointsList.get(6));
		
//		Map<Integer, PlayerPoints> result = map.entrySet()
//				  .stream()
//				  .sorted(Map.Entry.comparingByValue())
//				  .collect(Collectors.toMap(
//				    Map.Entry::getKey, 
//				    Map.Entry::getValue,
//				    (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//		System.out.println(map);
//		System.out.println(result);
//		System.out.println(computeRankings(map));
		
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		playerWrappers.add(null);
//		randomGameGrid = new Integer[10][10];
//		generateRandomGameGrid();
//		System.out.println();
//		for(int i = 0; i < randomGameGrid.length; i++) {
//			for(int j = 0; j < randomGameGrid[i].length; j++)
//				System.out.print(randomGameGrid[i][j] + " ");
//			System.out.println();
//		}
		
//		List<Game> playerGames = assignGamesToPlayer(playerWrappers.get(0));
//		for(Game game : playerGames)
//			System.out.println(game.getScoreAsString());
	}
}