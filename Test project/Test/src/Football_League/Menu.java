package Football_League;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.LocalDate;

public class Menu implements Serializable {
    private League league;
    private transient Scanner scanner;

    private MatchManager matchManager;

    public Menu(League league) {
        this.league = league;
        this.matchManager = new MatchManager(league);
        this.scanner = new Scanner(System.in);
    }
    public Menu(MatchManager matchManager1) {
        this.league = matchManager1.getLeague();
        this.matchManager = matchManager1;
        this.scanner = new Scanner(System.in);
    }

    public MatchManager getMatchManager() {
        return matchManager;
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("Main Menu:");
        System.out.println("1. Teams");
        System.out.println("2. Players");
        System.out.println("3. Matches");
        System.out.println("4. Table");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    public void start() throws IOException, ClassNotFoundException, FileNotFoundException {
        System.out.println("Welcome to football league simulator !");
        int choice;
        do {
            displayMenu();

            try {
                choice = scanner.nextInt();
                scanner.nextLine();

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();

                choice = -1;

            }


            switch (choice) {
                case 1:
                    handleTeamsMenu();
                    break;
                case 2:
                    handlePlayersMenu();
                    break;
                case 3:
                    handleMatchesMenu();
                    break;
                case 4:
                    handleTableMenu();
                    break;
                case 5:

                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    private void handlePlayersMenu() {
        int choice;
        do {
            System.out.println();
            System.out.println("Players Menu:");
            System.out.println("1. Add Player");
            System.out.println("2. Display Player's info");
            System.out.println("3. Edit Player");
            System.out.println("4. Search for a Player");
            System.out.println("5. Delete a Player");
            System.out.println("6. Back");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
                choice = -1;

            }

            switch (choice) {
                case 1:
                    if (league.teams.isEmpty()) {
                        System.out.println("There are no Teams to add a player to !");
                        break;
                    }
                    addPlayer();

                    break;
                case 2:

                    displayPlayerInfo();

                    break;

                case 3:
                    if (league.teams.isEmpty() || league.teams.stream().allMatch(team -> team.getPlayers().isEmpty())) {
                        System.out.println("There are no players to edit !");
                        break;
                    }
                    editPlayer();
                    break;
                case 4:
                    if (league.teams.isEmpty() || league.teams.stream().allMatch(team -> team.getPlayers().isEmpty())) {
                        System.out.println("There are no players to view !");
                        break;
                    }
                    searchPlayer();
                    break;
                case 5:
                    if (league.teams.isEmpty() || league.teams.stream().allMatch(team -> team.getPlayers().isEmpty())) {
                        System.out.println("There are no players to delete !");
                        break;
                    }
                    deletePlayer();
                    break;


                case 6:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }

    private void addPlayer() {
        System.out.println("Please enter the number of the team you want to assign the player to: (type 0 to back) ");
        league.viewTeams();
        int teamIndex;

        while (true) {
            try {
                System.out.print("Enter team number: ");
                teamIndex = scanner.nextInt();

                if (teamIndex == 0) {
                    return;
                } else if (teamIndex > 0 && teamIndex <= league.teams.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid team number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");


                scanner.nextLine();
            }
        }


        Team team = league.teams.get(teamIndex - 1);


        int positionNumber;
        do {
            try {
                System.out.println("Choose a position for the player:");
                displayPossiblePositions();
                System.out.println("Enter the position number:");
                positionNumber = scanner.nextInt();

                if (positionNumber > 4 || positionNumber < 1) {
                    System.out.println("Please enter a valid number");
                }

                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid position number.");
                scanner.nextLine();
                positionNumber = -1;
            }


        } while (positionNumber < 1 || positionNumber > 4);


        Player player = createPlayerByPosition(positionNumber);


        player.enterPlayerInformation();
        while (isPlayerIdDuplicate(player.getID())) {
            System.out.println("Player with ID " + player.getID() + " already exists. Please choose a unique ID.");
            int newId = scanner.nextInt();
            if (!isPlayerIdDuplicate(newId)) {
                player.setPlayerID(newId);
                break;
            }
        }
        while (isKitNumberDuplicate(player.getNumber(), team)) {
            System.out.println("Player with Number " + player.getNumber() + " already exists. Please choose a unique Number.");
            int newNum = scanner.nextInt();

            if (!isKitNumberDuplicate(newNum, team)) {
                player.setNumber(newNum);
                break;
            } else {
                System.out.println("Player with Number " + newNum + " already exists. Please choose a unique Number.");
            }
        }


        player.setTeam(team.getName());


        team.addPlayerToTeam(player);

        System.out.println("Player added successfully.");
        System.out.println();
    }

    public boolean isPlayerIdDuplicate(int playerId) {
        for (Team team : league.teams) {
            for (Player existingPlayer : team.getPlayers()) {
                if (existingPlayer.getID() == playerId) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isKitNumberDuplicate(int jerseyNumber, Team team) {
        for (Player existingPlayer : team.getPlayers()) {
            if (existingPlayer.getNumber() == jerseyNumber) {
                return true;
            }
        }
        return false;
    }


    private void editPlayer() {
        System.out.println("Please enter the team of the player you want to edit (type 0 to back)");
        league.viewTeams();

        int x;

        while (true) {

            x = scanner.nextInt();

            if (x == 0) {
                return;
            } else if (x > 0 && x <= league.teams.size()) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid team number.");
            }
        }
        Team t = league.teams.get(x - 1);
        System.out.println("Please enter the number of the player you want to edit");
        t.displayTeamPlayersWithID();
        int y;
        y = scanner.nextInt();
        Player p = t.players.get(y - 1);
        p.updatePlayerInformation();
        System.out.println("Player updated ");
        System.out.println();

    }

    private void deletePlayer() {
        System.out.println();
        System.out.println("Please enter the team of the player you want to delete (type 0 to go back)");
        league.viewTeams();

        int teamNumber;
        while (true) {
            try {
                System.out.print("Enter team number: ");
                teamNumber = scanner.nextInt();

                if (teamNumber == 0) {
                    return;
                } else if (teamNumber > 0 && teamNumber <= league.teams.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid team number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");

                scanner.nextLine();
            }
        }


        Team team = league.teams.get(teamNumber - 1);


        team.displayTeamPlayersWithID();

        System.out.print("Enter the number of the player to delete (type 0 to go back): ");
        int playerNumberToDelete = scanner.nextInt();

        if (playerNumberToDelete == 0) {
            return;
        }

        if (playerNumberToDelete >= 1 && playerNumberToDelete <= team.getPlayers().size()) {
            Player playerToDelete = team.getPlayers().get(playerNumberToDelete - 1);

            if (playerToDelete.equals(team.getCaptain())) {
                System.out.print("This player is the captain. Are you sure you want to delete the captain? (y/n): ");
                String confirmation = scanner.next();

                if (confirmation.equalsIgnoreCase("n")) {
                    System.out.println("Deletion canceled. The captain remains unchanged.");
                    return;
                } else if (confirmation.equalsIgnoreCase("y")) {
                    team.deletePlayer(playerToDelete.getID());
                    System.out.println("Player named " + playerToDelete.getPlayerName() + " (ID: " + playerToDelete.getID() +
                            ") has been deleted from team " + team.getName());


                    team.displayTeamPlayersWithID();
                    System.out.print("Enter the number of the new captain: ");
                    int newCaptainNumber = scanner.nextInt();

                    if (newCaptainNumber >= 1 && newCaptainNumber <= team.getPlayers().size()) {
                        Player newCaptain = team.getPlayers().get(newCaptainNumber - 1);
                        team.setCaptain(newCaptain);
                        System.out.println(newCaptain.getPlayerName() + " is now the captain.");
                    } else {
                        System.out.println("Invalid selection for the new captain. The captain remains unchanged.");
                    }

                    return;
                } else {
                    System.out.println("Invalid input. Deletion canceled. The captain remains unchanged.");
                    return;
                }
            }

            team.deletePlayer(playerToDelete.getID());
            System.out.println("Player named " + playerToDelete.getPlayerName() + " (ID: " + playerToDelete.getID() +
                    ") has been deleted from team " + team.getName());
        } else {
            System.out.println("Invalid player selection.");
        }
    }

    private void displayPossiblePositions() {
        System.out.println("1. Forward");
        System.out.println("2. Midfielder");
        System.out.println("3. Defender");
        System.out.println("4. Goalkeeper");
    }

    private Player createPlayerByPosition(int positionNumber) {
        switch (positionNumber) {
            case 1:
                return new Forward();
            case 2:
                return new Midfielder();
            case 3:
                return new Defender();
            case 4:
                return new GoalKeeper();
            default:
                throw new IllegalArgumentException("Invalid position number.");
        }
    }

    private void searchPlayer() {
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("Search by: (type 0 to back)");
                System.out.println("1. Player's name only");
                System.out.println("2. Player's name and team's name");

                int searchOption = scanner.nextInt();

                if (searchOption >= 0 && searchOption <= 2) {
                    validInput = true;

                    switch (searchOption) {
                        case 1:
                            System.out.println("Enter player's name:");
                            String playerName;
                            while (true) {
                                playerName = scanner.next();
                                if (validateName(playerName)) {
                                    break;
                                } else {
                                    System.out.println("Please enter a valid name.");
                                }
                            }

                            league.searchPlayerByName(playerName);
                            break;
                        case 2:
                            System.out.println("Enter team's name:");
                            String teamName;
                            while (true) {
                                teamName = scanner.next();
                                if (validateName(teamName)) {
                                    break;
                                } else {
                                    System.out.println("Please enter a valid name.");
                                }
                            }
                            System.out.println("Enter player's name:");
                            String playerNameWithTeam;
                            while (true) {
                                playerNameWithTeam = scanner.next();
                                if (validateName(playerNameWithTeam)) {
                                    break;
                                } else {
                                    System.out.println("Please enter a valid name.");
                                }
                            }

                            league.searchPlayerByNameAndTeam(playerNameWithTeam, teamName);
                            break;
                        case 0:
                            System.out.println("Returning to the main menu.");
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                            break;
                    }
                } else {
                    System.out.println("Invalid option. Please enter a valid search option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }
    }

    private void displayPlayerInfo() {
        do {
            if (league.teams.isEmpty() || league.teams.stream().allMatch(team -> team.getPlayers().isEmpty())) {
                System.out.println("There are no players to display !");
                break;
            }
            System.out.println("Please enter the team of the player you want to display (type 0 to back)");
            league.viewTeams();
            int x;
            while (true) {

                x = scanner.nextInt();

                if (x == 0) {
                    return;
                } else if (x > 0 && x <= league.teams.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid team number.");
                }
            }
            Team t = league.teams.get(x - 1);
            System.out.println("Please enter the number of the player you want to display");
            t.displayTeamPlayersWithID();
            int y = scanner.nextInt();
            Player p = t.players.get(y - 1);
            p.displayPlayerInformation();

            System.out.println("Do you want to display another player? (y/n)");
            char decide = scanner.next().charAt(0);

            if (decide != 'y') {
                break;
            }
        } while (true);
    }


    private void handleTeamsMenu() throws IOException, ClassNotFoundException {
        int choice;
        do {
            System.out.println();
            System.out.println("Teams Menu:");
            System.out.println("1. Create Team");
            System.out.println("2. Edit Team");
            System.out.println("3. View Teams");
            System.out.println("4. Display team's Matches");
            System.out.println("5. Display Team's Info");
            System.out.println("6. Delete a team");
            System.out.println("7. Back");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
                choice = -1;

            }

            switch (choice) {
                case 1:
                    createTeam();
                    break;
                case 2:
                    if (league.teams.isEmpty()) {
                        System.out.println("There are no Teams !");
                        break;
                    }
                    editTeam();
                    break;
                case 3:

                    league.viewTeams();
                    System.out.println("Total teams : " + (matchManager.getLeague().teams.size()));
                    break;
                case 4:
                    if (league.teams.isEmpty()) {
                        System.out.println("There are no Teams !");
                        break;
                    }
                    displayTeamMatches();
                    break;

                case 5:
                    if (league.teams.isEmpty()) {
                        System.out.println("There are no Teams !");
                        break;
                    }
                    displayTeamInfo();
                    break;
                case 6:
                    if (league.teams.isEmpty()) {
                        System.out.println("There are no Teams !");
                        break;
                    }
                    deleteTeam();
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    private void createTeam() {
        System.out.println("Please enter team name. (type 0 to back)");
        String name;
        while (true) {
            name = scanner.nextLine();

            if (name.equals("0")) {
                return;
            }

            if (validateName(name)) {
                break;
            }

            System.out.println("Team name must be a non-empty string without numbers or other symbols.");
        }
        int id = 0;

        while (true) {
            try {
                System.out.println("Please enter team ID:");


                id = scanner.nextInt();


                if (id <= 0) {
                    System.out.println("Invalid input. ID should be greater than zero.");
                    continue;
                }


                if (isTeamIdDuplicate(id)) {
                    System.out.println("Team with ID " + id + " already exists. Please choose a unique ID.");
                    continue;
                }


                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer for team ID.");


                scanner.nextLine();
            }
        }
        scanner.nextLine();
        String stadium;
        while (true) {
            try {
                System.out.println("Please enter team Stadium:");


                stadium = scanner.nextLine();


                if (!validateName(stadium)) {
                    System.out.println("Invalid input for team stadium. Make sure it is a non-empty string without numbers or other symbols.");
                }
                if (isStadiumNameDuplicate(stadium)) {
                    System.out.println("Team with Stadium " + stadium + " already exists. Please choose a unique Stadium.");
                }
                if (validateName(stadium) && !isStadiumNameDuplicate(stadium))
                    break;

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid name for team stadium.");
                scanner.nextLine();
            }
        }


        Team team = new Team(name, id, stadium);
        league.teams.add(team);

        System.out.println("Team " + name + " Created");
        matchManager.updateTeams(league.teams);

        System.out.println();

    }

    private void editTeam() {
        System.out.println();
        System.out.println("Please enter the number of the team you want to edit: ");
        league.viewTeams();
        int teamNum;
        while (true) {
            try {
                System.out.println("Enter your choice: ");
                teamNum = scanner.nextInt();
                if (teamNum == 0) {
                    return;
                } else if (teamNum > 0 && teamNum <= league.teams.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid team number.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");

                scanner.nextLine();
            }
        }
        Team team = league.teams.get(teamNum - 1);
        updateTeam(team);
    }

    public void updateTeam(Team team) {
        System.out.println("Update Team Information:");


        while (true) {
            try {
                System.out.print("Do you want to update the team name? (y/n): ");
                String updateTeamName = scanner.next().toLowerCase();
                if ("y".equals(updateTeamName)) {

                    scanner.nextLine();

                    System.out.print("Enter new team name: ");
                    String teamName = scanner.nextLine();

                    if (validateName(teamName)) {
                        team.setTeamName(teamName);
                        break;
                    } else {
                        System.out.println("Team name must be a non-empty string without numbers or other symbols.");
                    }


                } else if ("n".equals(updateTeamName)) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }


        while (true) {
            try {
                System.out.print("Do you want to update the team ID? (y/n): ");
                String updateTeamId = scanner.next().toLowerCase();
                if ("y".equals(updateTeamId)) {
                    while (true) {
                        try {

                            System.out.print("Enter new team ID: ");
                            int id = scanner.nextInt();
                            if (id <= 0) {
                                System.out.println("Invalid input. ID should be greater than zero.");
                                continue;
                            }


                            if (isTeamIdDuplicate(id)) {
                                System.out.println("Team with ID " + id + " already exists. Please choose a unique ID.");
                                continue;
                            }
                            team.setTeamId(id);
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid integer for team ID.");
                            scanner.nextLine();
                        }
                    }
                    break;
                } else if ("n".equals(updateTeamId)) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }
        while (true) {
            try {
                System.out.print("Do you want to update the team stadium? (y/n): ");
                String updateTeamStadium = scanner.next().toLowerCase();
                if ("y".equals(updateTeamStadium)) {
                    scanner.nextLine();

                    System.out.print("Enter new team stadium: ");
                    String teamStadium = scanner.nextLine();

                    if (validateName(teamStadium)) {
                        team.setStadium(teamStadium);
                        break;
                    } else {
                        System.out.println("Stadium name must be a non-empty string without numbers or other symbols.");
                    }
                } else if ("n".equals(updateTeamStadium)) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine();
            }

        }


        while (true) {
            try {
                System.out.print("Do you want to update the team captain? (y/n): ");
                String updateTeamCaptain = scanner.next().toLowerCase();
                if ("y".equals(updateTeamCaptain)) {
                    if (team.getPlayers().isEmpty()) {
                        System.out.println("There are no Players in the Team !");
                        break;
                    }
                    team.displayTeamPlayers();
                    team.updateTeamCaptain(scanner);
                    break;
                } else if ("n".equals(updateTeamCaptain)) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }

        System.out.println("Team information updated successfully!");
    }


    private boolean isTeamIdDuplicate(int id) {
        for (Team team : league.teams) {
            if (team.getTeamId() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean isStadiumNameDuplicate(String name) {
        for (Team team : league.teams) {
            if (team.getStadium().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void displayTeamMatches() {
        String displayAnotherTeam;
        do {
            System.out.println("Select a team to view matches:");
            System.out.println();
            league.viewTeams();

            System.out.print("Enter the number of the team: ");
            System.out.println();
            int teamNumber;
            while (true) {
                try {
                    teamNumber = scanner.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.next();
                }
            }

            if (teamNumber >= 1 && teamNumber <= league.teams.size()) {
                Team selectedTeam = league.teams.get(teamNumber - 1);
                System.out.println("Matches for Team \"" + selectedTeam.getName() + "\" :");

                boolean teamFound = false;

                for (Match match : matchManager.getMatches()) {
                    if (match.getHomeTeam().equals(selectedTeam) || match.getAwayTeam().equals(selectedTeam)) {
                        teamFound = true;

                        String homeAwayIndication = match.getHomeTeam().equals(selectedTeam) ? "Home" : "Away";
                        System.out.println(homeAwayIndication + " Match");

                        System.out.println(match);
                        System.out.println("Match Date :" + match.getMatchDate());
                        if (match.isSimulated()) {
                            System.out.println("Match Score : " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore());
                        } else {
                            System.out.println("Match Score : Not Played Yet");
                        }
                        System.out.println("------------------------");
                    }
                }

                if (!teamFound) {
                    System.out.println("No matches found for Team: " + selectedTeam.getName());
                }

            } else {
                System.out.println("Invalid team selection.");
            }

            System.out.println();
            while (true) {
                System.out.print("Do you want to display matches for another team? (y/n): ");
                try {
                    displayAnotherTeam = scanner.next();
                    if (displayAnotherTeam.equalsIgnoreCase("y") || displayAnotherTeam.equalsIgnoreCase("n")) {
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    scanner.next();
                }
            }

        } while (!displayAnotherTeam.equalsIgnoreCase("n"));
    }

    public void teamPlayers() {
        System.out.println("View Team's Players");


        league.viewTeams();


        Team teamSelected = null;
        while (true) {
            try {
                System.out.print("Enter team number to view players (type 0 to go back): ");
                int teamChoice = scanner.nextInt();

                if (teamChoice == 0) {
                    System.out.println("Returning to the main menu.");
                    return;
                } else if (teamChoice > 0 && teamChoice <= league.teams.size()) {
                    teamSelected = league.teams.get(teamChoice - 1);
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid team number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer for team number.");
                scanner.nextLine();
            }
        }


        if (teamSelected != null) {
            System.out.println("Players of " + teamSelected.getName() + ":");
            teamSelected.displayTeamPlayers();


            while (true) {
                System.out.print("Do you want to choose another team? (y/n): ");
                String chooseAnotherTeam = scanner.next().toLowerCase();

                if ("y".equals(chooseAnotherTeam)) {

                    teamPlayers();
                    return;
                } else if ("n".equals(chooseAnotherTeam)) {
                    System.out.println("Returning to the main menu.");
                    return;
                } else {
                    System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                }
            }
        }
    }

    private void displayTeamInfo() {
        do {
            if (league.teams.isEmpty()) {
                System.out.println("There are no Teams to display !");
                break;
            }
            System.out.println("Please enter the team you want to display (type 0 to back)");
            league.viewTeams();
            int x;
            while (true) {

                x = scanner.nextInt();

                if (x == 0) {
                    return;
                } else if (x > 0 && x <= league.teams.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid team number.");
                }
            }
            Team t = league.teams.get(x - 1);
            t.displayTeamInformation();


            System.out.println("Do you want to display another team? (y/n)");
            char decide = scanner.next().charAt(0);

            if (decide != 'y') {
                break;
            }
        } while (true);
    }

    private void deleteTeam() {
        System.out.println();
        System.out.println("Please enter the team of the team you want to delete (type 0 to go back)");
        league.viewTeams();

        int teamNumber;
        while (true) {
            try {
                System.out.print("Enter team number: ");
                teamNumber = scanner.nextInt();

                if (teamNumber == 0) {
                    return;
                } else if (teamNumber > 0 && teamNumber <= league.teams.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid team number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");

                scanner.nextLine();
            }
        }

        Team teamToDelete = league.teams.get(teamNumber - 1);

        System.out.println("Are you sure you want to delete the team " + teamToDelete.getName() + "? , all players of this team will be deleted (y/n): ");

        String confirmation;
        boolean validInput = false;

        while (true) {
            confirmation = scanner.next();

            if (confirmation.equalsIgnoreCase("y")) {
                league.teams.remove(teamToDelete);
                matchManager.updateTeams(league.teams);
                System.out.println("Team " + teamToDelete.getName() + " has been deleted.");
                Team.totalTeams--;
                validInput = true;
            } else if (confirmation.equalsIgnoreCase("n")) {
                System.out.println("Deletion canceled. The team remains unchanged.");
                validInput = true;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }

            if (validInput) {
                break;
            }
        }
    }

    private void handleMatchesMenu() {
        int choice;
        do {
            System.out.println("Matches Menu:");
            System.out.println("1. Create Match");
            System.out.println("2. Edit Match");
            System.out.println("3. Display all Matches");
            System.out.println("4. Search for a Match");
            System.out.println("5. Simulate Matches");
            System.out.println("6. Delete a Match");
            System.out.println("7. Back");
            System.out.print("Enter your choice: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
                choice = -1;

            }

            System.out.println();

            switch (choice) {
                case 1:

                    createMatch();
                    break;
                case 2:
                    if (matchManager.matches.isEmpty()) {
                        System.out.println("There is no Matches to edit !");
                        System.out.println();
                        break;
                    }
                    editMatch();
                    break;
                case 3:
                    if (matchManager.matches.isEmpty()) {
                        System.out.println("There is no Matches to view !");
                        System.out.println();
                        break;
                    }
                    displayMatches();
                    break;
                case 4:
                    displayMatchesOnDate();
                    break;
                case 5:
                    if (matchManager.matches.isEmpty()) {
                        System.out.println("There is no Matches to simulate !");
                        System.out.println();
                        break;
                    }
                    simulateMatch();
                    break;
                case 6:
                    if (matchManager.matches.isEmpty()) {
                        System.out.println("There is no Matches to delete !");
                        System.out.println();
                        break;
                    }
                    deleteMatch();
                    break;
                case 7:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
    }

    private void createMatch() {
        boolean inputMismatch;
        do {
            inputMismatch = false;
            try {
                System.out.println("Please enter the number for team 1: (Or 0 to back)");
                league.viewTeams();
                int x = scanner.nextInt();
                scanner.nextLine();
                if (x == 0)
                    break;

                if (x < 1 || x > league.teams.size()) {
                    System.out.println("Error: Invalid team number. Please enter a valid team number.");
                    inputMismatch = true;
                    continue;
                }

                Team t1 = league.teams.get(x - 1);

                Team t2;
                while (true) {
                    System.out.println("Please enter the number for team 2:");

                    int index = 1;

                    for (Team team : league.teams) {
                        if (!team.equals(t1)) {
                            System.out.println(index + ". " + team.getName());
                            index++;
                        } else {
                            System.out.println(index + ". " + team.getName() + "(already picked)");
                            index++;
                        }
                    }

                    int teamNum = scanner.nextInt();
                    if (teamNum < 1 || teamNum > league.teams.size() || t1 == league.teams.get(teamNum - 1)) {
                        System.out.println("Cannot make a match between the same Team !");
                    } else {
                        t2 = league.teams.get(teamNum - 1);
                        break;
                    }
                }

                String stadium = t1.getStadium();
                String randomReferee = matchManager.generateRandomReferee();

                LocalDate currentDate = LocalDate.now();

                LocalDate matchDate;
                do {

                    scanner.nextLine();

                    System.out.print("Enter the date for the match (YYYY-MM-DD): ");
                    String inputDate = scanner.nextLine();

                    try {
                        matchDate = LocalDate.parse(inputDate, DateTimeFormatter.ISO_LOCAL_DATE);
                        if (matchDate.isBefore(currentDate)) {
                            System.out.println("Error: The selected date is in the past. Please choose a future date.");
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Error: Invalid date format. Please enter the date in the specified format (YYYY-MM-DD).");
                        matchDate = null;
                    }
                } while (matchDate == null || matchDate.isBefore(currentDate));


                Match newMatch = new Match(t1, t2, stadium, randomReferee, MatchManager.getMatchIdCounter(), matchDate);
                int newID = MatchManager.getMatchIdCounter() + 1;
                MatchManager.setMatchIdCounter(newID);
                System.out.println("Match created!");

                matchManager.matches.add(newMatch);
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
                inputMismatch = true;
            }
        } while (inputMismatch);
    }

    public void editMatch() {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("Please enter the index of the match you want to edit: ");
        matchManager.displayAllMatches();
        int matchIndex;
        while (true) {
            try {
                System.out.println("Enter your choice (or 0 to back): ");
                matchIndex = scanner.nextInt();
                if (matchIndex == 0) {
                    return;
                } else if (matchIndex > 0 && matchIndex <= matchManager.matches.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid match index.");
                }

            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");

                scanner.nextLine();
            }
        }
        Match match = matchManager.matches.get(matchIndex - 1);
        updateMatch(match);
    }

    public void updateMatch(Match match) {
        Scanner scanner = new Scanner(System.in);
        String choice;

        while (true) {
            System.out.print("Do you want to update the date? (y/n): ");
            choice = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(choice)) {
                while (true) {
                    System.out.print("Please enter the new date (YYYY-MM-DD): ");
                    String inputDate = scanner.nextLine();

                    try {
                        LocalDate newDate = LocalDate.parse(inputDate, DateTimeFormatter.ISO_LOCAL_DATE);
                        if (newDate.isBefore(LocalDate.now())) {
                            System.out.println("Error: The selected date is in the past. Please choose an upcoming date.");
                        } else {
                            match.setMatchDate(newDate);
                            System.out.println("Date updated successfully.");
                            break;
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Error: Invalid date format. Please enter the date in the specified format.");
                    }
                }
                break;
            } else if ("n".equals(choice)) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            }
        }

        while (true) {
            System.out.print("Do you want to update the teams? (y/n): ");
            choice = scanner.nextLine();

            if ("y".equalsIgnoreCase(choice)) {
                System.out.println("Please enter the number of the team you want to assign the match to");

                break;
            } else if ("n".equalsIgnoreCase(choice)) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            }
        }

        while (true) {
            System.out.print("Do you want to update the referee? (y/n): ");
            choice = scanner.nextLine();
            if ("y".equalsIgnoreCase(choice)) {
                System.out.print("Please enter the new referee's name: ");
                try {
                    String newReferee = scanner.nextLine();
                    if (validateName(newReferee)) {
                        match.setReferee(newReferee);
                        System.out.println("Referee updated successfully.");
                        break;
                    } else {
                        System.out.println("Invalid name. Please enter a valid name for the referee.");
                    }
                } catch (java.util.NoSuchElementException e) {
                    System.out.println("Invalid input. Please enter a valid name.");
                    scanner.nextLine();
                }
            } else if ("n".equalsIgnoreCase(choice)) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            }
        }

        while (true) {
            System.out.print("Do you want to update the stadium? (y/n): ");
            choice = scanner.nextLine();
            if ("y".equalsIgnoreCase(choice)) {
                System.out.print("Please enter the new stadium name: ");
                String newStadium = scanner.nextLine();
                if (validateName(newStadium)) {
                    if (checkStadiumAvailability(newStadium, match.getMatchDate())) {
                        match.setStadium(newStadium);
                        System.out.println("Stadium updated successfully.");
                        break;
                    }
                } else {
                    System.out.println("Invalid name. Please enter a valid name for the stadium:");
                }
            } else if ("n".equalsIgnoreCase(choice)) {

                break;
            } else {
                System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            }
        }
        if (match.isSimulated()) {
            while (true) {
                System.out.print("Do you want to update the match score? (y/n): ");
                choice = scanner.nextLine().trim().toLowerCase();

                if ("y".equals(choice)) {
                    while (true) {
                        try {
                            if (match.getHomeTeamScore() > match.getAwayTeamScore()) {
                                match.getHomeTeam().setNumberOfPoints(match.getHomeTeam().getNumberOfPoints() - 3);
                            }
                            if (match.getHomeTeamScore() == match.getAwayTeamScore()) {
                                match.getHomeTeam().setNumberOfPoints(match.getHomeTeam().getNumberOfPoints() - 1);
                                match.getAwayTeam().setNumberOfPoints(match.getAwayTeam().getNumberOfPoints() - 1);
                            } else {
                                match.getAwayTeam().setNumberOfPoints(match.getAwayTeam().getNumberOfPoints() - 3);
                            }

                            match.getHomeTeam().setTotalGoalsScored(match.getHomeTeam().getTotalGoalsScored() - match.getHomeTeamScore());
                            match.getAwayTeam().setTotalGoalsScored(match.getAwayTeam().getTotalGoalsScored() - match.getAwayTeamScore());
                            match.getHomeTeam().setTotalGoalsReceived(match.getHomeTeam().getTotalGoalsReceived() - match.getAwayTeamScore());
                            match.getAwayTeam().setTotalGoalsReceived(match.getAwayTeam().getTotalGoalsReceived() - match.getHomeTeamScore());
                            System.out.print("Please enter the new home team score: ");
                            int newHomeScore = scanner.nextInt();
                            System.out.print("Please enter the new away team score: ");
                            int newAwayScore = scanner.nextInt();
                            scanner.nextLine();

                            if (newHomeScore > newAwayScore) {
                                match.getHomeTeam().setNumberOfPoints(match.getHomeTeam().getNumberOfPoints() + 3);
                            }
                            if (newHomeScore == newAwayScore) {
                                match.getHomeTeam().setNumberOfPoints(match.getHomeTeam().getNumberOfPoints() + 1);
                                match.getAwayTeam().setNumberOfPoints(match.getAwayTeam().getNumberOfPoints() + 1);
                            } else {
                                match.getAwayTeam().setNumberOfPoints(match.getHomeTeam().getNumberOfPoints() + 3);
                            }
                            match.getHomeTeam().setTotalGoalsScored(match.getHomeTeam().getTotalGoalsScored() + newHomeScore);
                            match.getAwayTeam().setTotalGoalsScored(match.getAwayTeam().getTotalGoalsScored() - newAwayScore);
                            match.getHomeTeam().setTotalGoalsReceived(match.getHomeTeam().getTotalGoalsReceived() - newAwayScore);
                            match.getAwayTeam().setTotalGoalsReceived(match.getAwayTeam().getTotalGoalsReceived() - newHomeScore);

                            match.setHomeTeamScore(newHomeScore);
                            match.setAwayTeamScore(newAwayScore);


                            System.out.println("Match score updated successfully.");
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Invalid input. Please enter valid integer scores.");
                            scanner.nextLine();
                        }
                        break;
                    }
                } else if ("n".equals(choice)) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                }
            }
        }

        System.out.println("Match information updated successfully!");
    }

    private void displayMatches() {
        matchManager.displayAllMatches();
    }

    private void displayMatchesOnDate() {
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Enter the date (yyyy-MM-dd) to display matches: ");
                String inputDateStr = scanner.next();
                LocalDate inputDate = LocalDate.parse(inputDateStr);

                System.out.println("Matches on " + inputDate + ":");

                boolean foundMatches = false;


                for (Match match : matchManager.getMatches()) {
                    if (match.getMatchDate().isEqual(inputDate) ) {
                        foundMatches = true;

                        System.out.println(match);
                        System.out.println("Match Date :" + match.getMatchDate());
                        if (match.isSimulated()) {
                            System.out.println("Match Score : " + match.getHomeTeamScore() + " - " + match.getAwayTeamScore());
                        } else {
                            System.out.println("Match Score : Not Played Yet");
                        }
                        System.out.println("------------------------");
                    }
                }

                if (!foundMatches) {
                    System.out.println("No matches on " + inputDate + ".");
                }

                validInput = true;

            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd. Try again.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }


    private boolean checkStadiumAvailability(String stadiumName, LocalDate matchDate) {

        while (!stadiumExists(stadiumName, league.teams)) {
            System.out.println("Error: Stadium with the provided name does not exist.");
            System.out.print("Please enter a valid stadium name: ");
            stadiumName = scanner.nextLine();
        }

        for (Match existingMatch : matchManager.matches) {
            if (existingMatch.getMatchDate().equals(matchDate) && existingMatch.getStadium().equalsIgnoreCase(stadiumName)) {
                System.out.println("Error: Another match is already scheduled in the same stadium on the selected date.");
                return false;
            }
        }


        return true;
    }

    private boolean stadiumExists(String stadiumName, List<Team> teams) {
        for (Team team : teams) {
            if (team.getStadium().equalsIgnoreCase(stadiumName)) {
                return true;
            }
        }
        return false;
    }

    public void deleteMatch() {
        int matchIndex;
        while (true) {
            try {
                System.out.print("Enter the number of the match you want to delete (0 to back): ");
                matchManager.displayAllMatches();
                System.out.println("Enter your choice :");

                matchIndex = scanner.nextInt();

                if (matchIndex == 0) {
                    System.out.println("Deletion canceled.");
                    break;
                } else if (matchIndex > 0 && matchIndex <= matchManager.matches.size()) {
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid match number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }

        Match matchToDelete = matchManager.matches.get(matchIndex - 1);
        matchManager.displayMatchDetails(matchToDelete);
        scanner.nextLine();

        while (true) {
            System.out.print("Are you sure you want to delete this match? (y/n): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(confirmation)) {

                if (matchToDelete.getHomeTeamScore() > matchToDelete.getAwayTeamScore()) {
                    matchToDelete.getHomeTeam().setNumberOfPoints(matchToDelete.getHomeTeam().getNumberOfPoints() - 3);
                } else if (matchToDelete.getHomeTeamScore() == matchToDelete.getAwayTeamScore()) {
                    matchToDelete.getHomeTeam().setNumberOfPoints(matchToDelete.getHomeTeam().getNumberOfPoints() - 1);
                    matchToDelete.getAwayTeam().setNumberOfPoints(matchToDelete.getAwayTeam().getNumberOfPoints() - 1);
                } else {
                    matchToDelete.getAwayTeam().setNumberOfPoints(matchToDelete.getAwayTeam().getNumberOfPoints() - 3);
                }


                matchToDelete.getHomeTeam().setTotalGoalsScored(matchToDelete.getHomeTeam().getTotalGoalsScored() - matchToDelete.getHomeTeamScore());
                matchToDelete.getAwayTeam().setTotalGoalsScored(matchToDelete.getAwayTeam().getTotalGoalsScored() - matchToDelete.getAwayTeamScore());
                matchToDelete.getHomeTeam().setTotalGoalsReceived(matchToDelete.getHomeTeam().getTotalGoalsReceived() - matchToDelete.getAwayTeamScore());
                matchToDelete.getAwayTeam().setTotalGoalsReceived(matchToDelete.getAwayTeam().getTotalGoalsReceived() - matchToDelete.getHomeTeamScore());
                matchManager.matches.remove(matchToDelete);
                System.out.println("Deletion successful.");
                break;
            } else if ("n".equals(confirmation)) {
                System.out.println("Deletion canceled.");
                break;
            } else {
                System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

    private void simulateMatch() {
        boolean inputMismatch;
        do {
            inputMismatch = false;
            try {
                System.out.println("Simulate Matches Menu:");
                System.out.println("1. Simulate matches till a certain date");
                System.out.println("2. Simulate all matches");
                System.out.println();
                System.out.print("Choose an option (or 0 to back) :");

                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        simulateMatchesTillDate();
                        break;
                    case 2:
                        simulateAllMatches();
                        break;
                    case 0:
                        System.out.println("Returning to the previous menu.");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose a valid option");
                        inputMismatch = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
                inputMismatch = true;
                scanner.nextLine();
            }
        } while (inputMismatch);
    }


    public void simulateAllMatches() {
        System.out.println("Simulating all matches..");

        for (Match match : matchManager.getMatches()) {
            simulateMatchScore(match);
        }
    }

    public void simulateMatchesTillDate() {
        System.out.println("Simulate Matches Till Date:");


        LocalDate simulationDate = null;
        while (true) {
            try {
                displayMatches();
                System.out.print("Enter the simulation date (YYYY-MM-DD) or 0 to go back: ");
                String inputDate = scanner.nextLine();

                if (inputDate.equals("0")) {
                    return;
                }

                simulationDate = LocalDate.parse(inputDate, DateTimeFormatter.ISO_LOCAL_DATE);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date format. Please enter a valid date or 0 to go back.");
            }
        }


        int simulatedMatchesCount = 0;
        for (Match match : matchManager.getMatches()) {
            if (match.getMatchDate().isEqual(simulationDate) || match.getMatchDate().isBefore(simulationDate) && !match.isSimulated()
            ) {
                simulateMatchScore(match);
                match.setSimulated(true);
                simulatedMatchesCount++;
            }
        }

        System.out.println("Simulated " + simulatedMatchesCount + " matches till " + simulationDate + ".");
    }

    private void simulateMatchScore(Match match) {
        if(match.getHomeTeam().players.size()<1 || match.getAwayTeam().players.size()<1 )
        {
            System.out.println("Each Team should have at least 11 Players to simulate!");
        }
        else {
            match.getHomeTeam().setMatchesPlayed(match.getHomeTeam().getMatchesPlayed() + 1);
            match.getAwayTeam().setMatchesPlayed(match.getAwayTeam().getMatchesPlayed() + 1);
            Random random = new Random();
            int homeTeamScore = random.nextInt(6);
            int awayTeamScore = random.nextInt(6);
            match.setSimulated(true);
            distributeGoals(match.getHomeTeam(), homeTeamScore);
            distributeGoals(match.getAwayTeam(), awayTeamScore);

            match.setHomeTeamScore(homeTeamScore);
            match.setAwayTeamScore(awayTeamScore);


            updateTeamStats(match);


            System.out.println(match.getHomeTeam().getName() + " " + homeTeamScore + " - " +
                    awayTeamScore + " " + match.getAwayTeam().getName());
        }
    }
    private void distributeGoals(Team team, int goals) {
        List<Player> players = team.getPlayers();
        Random random = new Random();

        for (int i = 0; i < goals; i++) {
            int randomPlayerIndex = random.nextInt(players.size());
            Player scorer = players.get(randomPlayerIndex);
            scorer.setGoalScored(scorer.getGoalsScored() + 1);
        }
    }

    private void updateTeamStats(Match match) {
        int homeTeamScore = match.getHomeTeamScore();
        int awayTeamScore = match.getAwayTeamScore();

        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();

        homeTeam.setTotalGoalsScored(homeTeam.getTotalGoalsScored() + homeTeamScore);
        homeTeam.setTotalGoalsReceived(homeTeam.getTotalGoalsReceived() + awayTeamScore);

        awayTeam.setTotalGoalsScored(awayTeam.getTotalGoalsScored() + awayTeamScore);
        awayTeam.setTotalGoalsReceived(awayTeam.getTotalGoalsReceived() + homeTeamScore);

        if (homeTeamScore > awayTeamScore) {
            homeTeam.setNumberOfPoints(homeTeam.getNumberOfPoints() + 3);
        } else if (homeTeamScore < awayTeamScore) {
            awayTeam.setNumberOfPoints(awayTeam.getNumberOfPoints() + 3);

        } else {
            homeTeam.setNumberOfPoints(homeTeam.getNumberOfPoints() + 1);
            awayTeam.setNumberOfPoints(awayTeam.getNumberOfPoints() + 1);

        }
    }

    public void handleTableMenu() {
        System.out.println();
        int choice = 0;
        do {
            try {
                System.out.println("League Table Menu:");
                System.out.println("1. Display League Standings");
                System.out.println("2. Display Average Age Standings");
                System.out.println("3. Display Top Teams Goals");
                System.out.println("4. Display Top Goal Scorers");
                System.out.println("5. Display Top Clean Sheets");
                System.out.println("6. Reset League");
                System.out.println("7. Back");

                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();



                switch (choice) {
                    case 1:
                        league.displayLeagueStandings();
                        break;
                    case 2:
                        displayAverageAgeStandings();
                        break;
                    case 3:
                        displayTeamsGoals();
                        break;
                    case 4:
                        displayTopGoalScorers();
                        break;
                    case 5:
                        displayTopCleanSheets();
                        break;
                    case 6:
                        if ( league.resetLeague()) {
                            matchManager.resetMatches();
                        }
                        break;
                    case 7:
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        } while (choice != 7);
    }
    private void displayAverageAgeStandings() {

        league.sortTeamsByAverageAge();


        System.out.println("Average Age Standings:");
        System.out.printf("%-20s%-15s%n", "Team", "Avg Age");


        for (Team team : league.teams) {
            System.out.printf("%-20s%-15.2f%n", team.getTeamName(), team.calculateAverageAge());
        }
    }


    public void displayTopCleanSheets() {
        league.sortTeamsByLeastGoalsReceived();
        int count = 0;

        System.out.printf("%-20s%-20s%-10s%n", "Team", "Team GK", "GA");

        for (Team team : league.teams) {
            GoalKeeper goalkeeper = team.getGK();

            if (goalkeeper != null) {
                System.out.printf("%-20s%-20s%-10d%n", team.getTeamName(), goalkeeper.toString(), team.getTotalGoalsReceived());
                count++;
            }

            if (count >= 5) {
                break;
            }
        }
    }
    public void displayTeamsGoals() {
        league.sortTeamsByMostGoalsScored();

        System.out.println("Teams and Goals For:");
        System.out.printf("%-20s%-10s%n", "Team", "GF");

        for (Team team : league.teams) {
            System.out.printf("%-20s%-10d%n", team.getTeamName(), team.getTotalGoalsScored());
        }
    }
    public void displayTopGoalScorers() {
        List<Player> topGoalScorers = league.sortPlayersGoals();

        System.out.println("Top 5 Goal Scorers:");
        System.out.printf("%-20s%-10s%-10s%n", "Player", "Team", "Goals Scored");

        int count = 0;
        for (Player player : topGoalScorers) {
            System.out.printf("%-20s%-10s%-10d%n",
                    player.getPlayerName(),
                    player.getTeamName(),
                    player.getGoalsScored());

            count++;
            if (count >= 5) {
                break;
            }
        }
    }





    private boolean validateName(String name) {
        return !name.trim().isEmpty() && name.matches("[a-zA-Z\\s-]+");
    }



}
