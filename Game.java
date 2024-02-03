import java.io.*;
import java.util.*;

public class Game {

    private static Scanner scanner = new Scanner(System.in);
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Witaj w grze w zgadywanie liczb.");

        System.out.println("Podaj ilosc graczy:");
        int amountOfPlayers = scanner.nextInt();

        Player[] players = new Player[amountOfPlayers];
        for (int i = 0; i < amountOfPlayers; i++) {
            System.out.println("Podaj swoj nick:");

            String playerNickname = scanner.next();

            players[i] = loadPlayer(playerNickname);
        }


        while (true) {
            int mode;
            if (players.length == 1) {
                System.out.println("Wybierz tryb gry (1 - odgadywanie, 2 - zgadywanie przez komputer, 3 - mieszana z komputerem) 99. Wyjscie z gry");
                mode = scanner.nextInt();
            } else {
                System.out.println("1. Rozgrywka multiplayer - zgadujecie na przemian. 99. Wyjscie z gry");
                mode = scanner.nextInt();
            }

            if (mode == 99) {
                System.out.println("Do zobaczenia!");
                break;
            }


            Player computer = loadComputer();

            System.out.println("Wybierz tryb trudnosci (1 - latwy (0 - 100), 2 - normalny (0 - 10000), 3 - trudny (0 - 1000000): 4 - zaawansowany");

            int difficulty = scanner.nextInt();

            int maxBound = 0;
            int origin = 0;
            switch (difficulty) {
                case 1:
                    maxBound = 100;
                    break;
                case 2:
                    maxBound = 10000;
                    break;
                case 3:
                    maxBound = 1000000;
                    break;
                case 4:
                    System.out.println("Podaj dolna granice: ");
                    origin = scanner.nextInt();
                    System.out.println("Podaj gorna granice: ");
                    maxBound = scanner.nextInt();
            }

            if (maxBound < origin) {
                System.err.println("Gorna granica jest nizsza niz dolna granica!");
                break;
            }

            switch (mode) {
                case 1:
                    if (players.length > 1) {
                        playMultiplayer(players, maxBound, origin);
                    } else {
                        playNormalMode(players[0], maxBound, origin);
                    }
                    break;
                case 2:
                    playComputerChooses(computer, maxBound, origin);
                    break;
                case 3:
                    playAlternateMode(players[0], computer, maxBound, origin);
                    break;
            }
        }
    }

    private static void playMultiplayer(Player[] players, int maxBound, int origin) {
        System.out.println("Wybierz tryb 1. Nieturniejowy 2. Turniejowy");

        int choice = scanner.nextInt();

        if (choice == 1) {
            playMultiplayerWithoutTournament(players, maxBound, origin);
        } else if (choice == 2){
            playTournament(players, maxBound, origin);
        }
    }

    private static void playTournament(Player[] players, int maxBound, int origin) {
        System.out.println("Wybrano tryb turniejowy!");
        System.out.println("Podaj tryb rozgrywki:");
        System.out.println("1. BO1 2. BO3 3. BO5 4. Drabinka");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                playBestOf(1, players, origin, maxBound);
                break;
            case 2:
                playBestOf(3, players, origin, maxBound);
                break;
            case 3:
                playBestOf(5, players, origin, maxBound);
                break;
            case 4:
                playLadderMode(players, origin, maxBound);
        }
    }

    private static void playLadderMode(Player[] players, int origin, int maxBound) {
        int numberToGuess = generateRandomNumber(origin, maxBound);
        for (Player player : players) {
            if (player.isMaster()) {
                System.out.println("Znaleziono mistrza: " + player.getNickname());
                System.out.println("Gdy wygra dostanie 2 wygrane zamiast 1.");
            }
        }
    }

    private static void playBestOf(int bestOfFormat, Player[] players, int origin, int maxBound) {
        int winsToGet = bestOfFormat / 2 + 1;
        System.out.println("Gra do " + winsToGet + " wygranych.");
        int playerOneWins = 0;
        int playerTwoWins = 0;
        int numberToGuess = generateRandomNumber(origin, maxBound);

        for (int i = 0; i < 2; i++) {
            if (players[i].isMaster()) {
                System.out.println("Znaleziono mistrza: " + players[i].getNickname());
                System.out.println("Gdy wygra dostanie 2 wygrane zamiast 1.");
            }
        }

        while (playerOneWins < winsToGet && playerTwoWins < winsToGet) {

            for (int i = 0; i < 2; i++) {
                Player player = players[i];
                System.out.println("Kolej gracza: " + player.getNickname());
                int choice = scanner.nextInt();
                player.setTries(player.getTries() + 1);

                if (choice == numberToGuess) {

                    if (player.getBestScore() == 0 || player.getTries() < player.getBestScore()) {

                        player.setBestScore(player.getTries());
                        System.out.println("Gratulacje! Uzyskałeś nowy najlepszy wynik!");
                    }

                    int winsToAdd = 1;
                    if (player.isMaster()) {
                        System.out.println("Master dostal wygrana w gratisie do statystyk");
                        winsToAdd = 2;
                    }
                    player.setNumberOfWins(player.getNumberOfWins() + winsToAdd);


                    numberToGuess = generateRandomNumber(origin, maxBound);
                    if (i == 0) {
                        playerOneWins++;

                        if (playerOneWins == winsToGet) {
                            System.out.println("Turniej wygral: " + player.getNickname());
                            System.out.println(player.getNickname() + " zostal mistrzem.");
                            player.setMaster(true);
                            players[1].setMaster(false);
                            savePlayer(player);
                            savePlayer(players[1]);
                            break;
                        }
                    } else {
                        playerTwoWins++;

                        if (playerTwoWins == winsToGet) {
                            System.out.println("Turniej wygral: " + player.getNickname());
                            System.out.println(player.getNickname() + " zostal mistrzem.");
                            player.setMaster(true);
                            players[0].setMaster(false);
                            savePlayer(player);
                            savePlayer(players[0]);
                            break;
                        }
                    }

                    System.out.println("Runde wygral: " + player.getNickname());
                    System.out.println("Wynik: " + playerOneWins + " - " + playerTwoWins);
                } else if (choice < numberToGuess) {
                    System.out.println("Za mało! Spróbuj jeszcze raz.");
                } else {
                    System.out.println("Za dużo! Spróbuj jeszcze raz.");
                }
            }
        }
    }

    private static void playMultiplayerWithoutTournament(Player[] players, int maxBound, int origin) {
        int numberToGuess = generateRandomNumber(origin, maxBound);
        boolean guessed = false;
        String winnersNickname = "";

        List<Player> playersList = Arrays.asList(players);
        Collections.shuffle(playersList);
        players = playersList.toArray(players);

        for (Player player : players) {
            if (player.isLeader()) {
                System.out.println("Znaleziono lidera o nicku: " + player.getNickname());
                System.out.println("Bedzie on mial 2 ruchy z rzedu.");
            }
            if (player.isMaster()) {
                System.out.println("Znaleziono mastera o nicku: " + player.getNickname());
                System.out.println("Wiadomosci dla niego beda wyroznione innym kolorem.");
            }
        }

        System.out.print("Zgadnij liczbę (" + origin + "-" + maxBound + "): ");
        while (!guessed) {
            for (Player player : players) {
                int moves = player.isLeader() ? 2 : 1;

                for (int i = 0; i < moves; i++) {
                    if (player.isMaster()) {
                        System.out.print("\u001B[31m");
                    } else {
                        System.out.print("\u001B[0m");
                    }
                    System.out.println("Kolej gracza: " + player.getNickname());
                    int choice = scanner.nextInt();
                    player.setTries(player.getTries() + 1);

                    if (choice == numberToGuess) {
                        guessed = true;
                        winnersNickname = player.getNickname();
                        System.out.println("Brawo! Wygral gracz o nicku: " + player.getNickname());
                        System.out.println("Brawo! Zgadłeś liczbę " + numberToGuess + " po " + player.getTries() + " próbach.");

                        if (player.getBestScore() == 0 || player.getTries() < player.getBestScore()) {
                            player.setBestScore(player.getTries());
                            player.setNumberOfWins(player.getNumberOfWins() + 1);
                            player.setLeader(true);
                            savePlayer(player);
                            System.out.println("Gratulacje! Uzyskałeś nowy najlepszy wynik!");
                        }
                        break;
                    } else if (choice < numberToGuess) {
                        System.out.println("Za mało! Spróbuj jeszcze raz.");
                    } else {
                        System.out.println("Za dużo! Spróbuj jeszcze raz.");
                    }
                }
                if (guessed) {
                    break;
                }
            }
        }

        for (Player player : players) {
            if (!player.getNickname().equals(winnersNickname)) {
                player.setLeader(false);
                player.setNumberOfLoses(player.getNumberOfLoses() + 1);
                savePlayer(player);
            }
        }
    }

    private static void playNormalMode(Player player, int maxBound, int origin) {
        System.out.println("Tryb: Normalny tryb.");

        int numberToGuess = generateRandomNumber(origin, maxBound);
        int tries = 0;
        boolean guessed = false;

        while (!guessed) {
            System.out.print("Zgadnij liczbę (" + origin + "-" + maxBound + "): ");
            int choice = scanner.nextInt();
            tries++;


            if (choice == numberToGuess) {
                guessed = true;
                System.out.println("Brawo! Zgadłeś liczbę " + numberToGuess + " po " + tries + " próbach.");

                if (player.getBestScore() == 0 || tries < player.getBestScore()) {
                    player.setBestScore(tries);
                    savePlayer(player);
                    System.out.println("Gratulacje! Uzyskałeś nowy najlepszy wynik!");
                }
            } else if (choice < numberToGuess) {
                System.out.println("Za mało! Spróbuj jeszcze raz.");
            } else {
                System.out.println("Za dużo! Spróbuj jeszcze raz.");
            }
        }
    }

    private static void playComputerChooses(Player computer, int maxBound, int origin) {
        System.out.println("Tryb: Zgadywanie przez komputer.");

        int numberFromPlayer = loadNumberFromPlayer("Podaj liczbę do zgadnięcia (" + origin + "-" + maxBound + "): ", origin, maxBound);

        int bound = maxBound;
        int guesses = 0;
        boolean guessed = false;

        while (!guessed) {
            int propozycja = generateRandomNumber(origin, bound);
            guesses++;

            System.out.println("Czy to " + propozycja + "? (tak/malo/duzo)");
            String answer = scanner.next().toLowerCase();

            switch (answer) {
                case "tak" -> {
                    System.out.println("Gratulacje! Komputer zgadł liczbę " + numberFromPlayer + " po " + guesses + " próbach.");
                    if (computer.getBestScore() == 0 || guesses < computer.getBestScore()) {
                        computer.setBestScore(guesses);
                        savePlayer(computer);
                        System.out.println("Gratulacje! Uzyskałeś nowy najlepszy wynik!");
                    }
                    guessed = true;
                }
                case "malo" -> {
                    System.out.println("Za mało! Spróbuj jeszcze raz.");
                    origin = propozycja + 1;
                }
                case "duzo" -> {
                    System.out.println("Za dużo! Spróbuj jeszcze raz.");
                    bound = propozycja - 1;
                }
                default -> System.out.println("Nieprawidłowa odpowiedź. Odpowiedz 'tak', 'malo' lub 'duzo'.");
            }
        }
    }

    private static void playAlternateMode(Player player, Player computer, int maxBound, int origin) {
        System.out.println("Tryb: Gra mieszana.");
        boolean playerStarts = generateRandomNumber(0, 1) == 1;

        int numberToGuess = generateRandomNumber(0, maxBound);
        int bound = maxBound;
        int playerGuesses = 0;
        int computerGuesses = 0;
        boolean guessed = false;

        System.out.print("Zgadnij liczbę (" + origin + "-" + maxBound + "): ");
        while (!guessed) {
            if (playerStarts) {
                System.out.println("Kolej gracza.");
                int choice = scanner.nextInt();
                playerGuesses++;
                if (choice == numberToGuess) {
                    guessed = true;
                    System.out.println("Brawo! Pokonales komputer.");
                    System.out.println("Brawo! Zgadłeś liczbę " + numberToGuess + " po " + playerGuesses + " próbach.");

                    player.setNumberOfWins(player.getNumberOfWins() + 1);
                    computer.setNumberOfLoses(computer.getNumberOfLoses() + 1);
                    if (player.getBestScore() == 0 || playerGuesses < player.getBestScore()) {
                        player.setBestScore(playerGuesses);

                        System.out.println("Gratulacje! Uzyskałeś nowy najlepszy wynik!");
                    }
                } else if (choice < numberToGuess) {
                    System.out.println("Za mało!");
                    origin = choice + 1;
                } else {
                    System.out.println("Za dużo!");
                    bound = choice - 1;
                }


                playerStarts = false;
            } else {
                System.out.println("Kolej komputera");
                int computerGuess = generateRandomNumber(origin, bound);
                System.out.println("Komputer wybral: " + computerGuess);
                computerGuesses++;

                if (computerGuess == numberToGuess) {
                    guessed = true;
                    System.out.println("Porazka! Komputer wygral.");
                    System.out.println("Porazka! Komputer zgadl liczbę " + numberToGuess + " po " + computerGuesses + " próbach.");

                    computer.setNumberOfWins(computer.getNumberOfWins() + 1);
                    player.setNumberOfLoses(player.getNumberOfLoses() + 1);
                    if (computer.getBestScore() == 0 || computerGuesses < computer.getBestScore()) {
                        computer.setBestScore(computerGuesses);
                        System.out.println("Komputer uzyskał nowy najlepszy wynik!");
                    }
                } else if (computerGuess < numberToGuess) {
                    System.out.println("Za mało!");
                    origin = computerGuess + 1;
                } else {
                    System.out.println("Za dużo!");
                    bound = computerGuess - 1;
                }


                playerStarts = true;
            }
        }
        savePlayer(player);
        savePlayer(computer);
    }

    private static int loadNumberFromPlayer(String message, int min, int max) {
        System.out.print(message);
        return scanner.nextInt();
    }

    private static int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static Player loadPlayer(String nickname) {
        File file = new File(nickname + ".txt");
        Player player = new Player(nickname);

        if (file.exists()) {
            int bestScore = 0;
            int numberOfWins = 0;
            int numberOfLoses = 0;
            boolean isLeader = false;
            boolean isMaster = false;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                bestScore = Integer.parseInt(reader.readLine());
                numberOfWins = Integer.parseInt(reader.readLine());
                numberOfLoses = Integer.parseInt(reader.readLine());
                isLeader = Boolean.parseBoolean(reader.readLine());
                isMaster = Boolean.parseBoolean(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Znaleziono dane o graczu z nickiem: " + nickname);
            System.out.println("Wybierz opcje (1 lub 2):");
            System.out.println("1. Wczytaj swój najlepszy wynik");
            System.out.println("2. Zacznij rozgrywke od nowa i usun najlepszy wynik.");
            String choice = scanner.next();
            if (choice.equals("1")) {
                player.setBestScore(bestScore);
                player.setNumberOfWins(numberOfWins);
                player.setNumberOfLoses(numberOfLoses);
                player.setLeader(isLeader);
                player.setMaster(isMaster);
            } else if (choice.equals("2")) {
                file.delete();
                player.setBestScore(0);
            }
        }
        return player;
    }

    private static void savePlayer(Player player) {
        File file = new File(player.getNickname() + ".txt");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(player.getBestScore() + "\n");
            writer.write(player.getNumberOfWins() + "\n");
            writer.write(player.getNumberOfLoses() + "\n");
            writer.write(player.isLeader() + "\n");
            writer.write(player.isMaster() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Player loadComputer() {
        File file = new File("computer.txt");
        Player computer = new Player("computer");

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                computer.setBestScore(Integer.parseInt(reader.readLine()));
                computer.setNumberOfWins(Integer.parseInt(reader.readLine()));
                computer.setNumberOfLoses(Integer.parseInt(reader.readLine()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return computer;
    }
}
