package com.stettler.scopa;

import java.util.*;
import java.util.stream.Collectors;

public class Gameplay {

    Deck deck = new Deck();
    Scanner input = new Scanner(System.in);
    private static final String TABLE_CMD = "table";
    Player player1 = new Player();
    Player player2 = new Player();
    Player currentPlayer;

    Display table = new Display();
    int lastTrick = 0;
    List<Card> tableCards = new ArrayList<>();


    public static void main(String[] args) {
        Gameplay game = new Gameplay();
        game.playTheGame();
    }

    void playTheGame() {

        int turnCount = 0;
        int playedCard = 0;
        int roundCount = 0;
        String pickup;

        instructions();
        System.out.print("Enter Player #1 name: ");
        player1.setName(input.nextLine());
        player1.setName(nameCheck(player1.getName()));

        System.out.print("Enter Player #2 name: ");
        player2.setName(input.nextLine());
        player2.setName(nameCheck(player2.getName()));

        while (!winner()) {

            for (int i = 0; i < 4; i++) {
                tableCards.add(deck.draw());
            }

            while (deck.hasNext()) {

                turnCount = 0;


                for (int i = 0; i < 3; i++) {
                    player1.deal(deck.draw());
                    player2.deal(deck.draw());
                }

                while (turnCount < 6) {

                    if(roundCount % 2 == 0){
                        table.clearScreen();
                        player1First(turnCount);
                    }
                    else{
                        table.clearScreen();
                        player2First(turnCount);
                    }

                    Move move = Move.INVALID;


                    System.out.println("It is " + currentPlayer.getName() + "'s turn.");
                    while(move.equals(Move.INVALID)){
                        Scanner input = new Scanner(System.in);
                        System.out.println("Play: ");
                        try {
                            playedCard = input.nextInt();
                            input.nextLine();
                        } catch (InputMismatchException ex) {
                            System.out.println("Please use position numbers to play a card.");
                            continue;
                        }

                        System.out.println("For: ");
                        pickup = input.nextLine();

                        move = createMoveCommand(playedCard, Arrays.asList(pickup.split("\\s+")));
                    }
                    if(tableCards.isEmpty()) {
                        currentPlayer.setScore(currentPlayer.getScore() + 1);
                        System.out.println(currentPlayer.getName() + " got a Scopa!");
                    }
                    turnCount++;
                }
            }
            if(!tableCards.isEmpty()) {
                if (lastTrick == 1){
                    player1.play(Optional.empty(), tableCards);
                    tableCards.clear();
                }
                else {
                    player2.play(Optional.empty(), tableCards);
                    tableCards.clear();
                }
            }

            if(player1.getPrimesSum() > player2.getPrimesSum()){
                player1.setScore(player1.getScore() + 1);
            }
            else if(player1.getPrimesSum() < player2.getPrimesSum()) {
                player2.setScore(player2.getScore() + 1);
            }
            scoring(player1);
            scoring(player2);

            System.out.println("At the end of round " + (roundCount + 1) + " the  score is:");
            System.out.println(player1.getName() + " " + player1.getScore());
            System.out.println(player2.getName() + " " + player2.getScore());

            deck = new Deck();
            roundCount++;
        }

        System.out.println("It looks like we have a winner! Yay David! The final score is " + player1.getName() + " " + player1.getScore() + " and " + player2.getName() + " " + player2.getScore());

    }

    public Move createMoveCommand(int play, List<String> pickups) {

        if (play > this.currentPlayer.getHand().size() || play < 1) {
            System.out.println(String.format("You don't have that card. Valid entry is 1 to %d", this.currentPlayer.getHand().size()));
            return Move.INVALID;
        }

        // User entered table.
        if (pickups.get(0).toLowerCase().trim().equals(TABLE_CMD) && pickups.size() == 1) {
            return handleDiscard(new Discard(this.currentPlayer.getHand().get(play - 1)));
        }

        Pickup pickup = new Pickup();
        pickup.setPlayerCard(this.currentPlayer.getHand().get(play - 1));

        for(String c: pickups) {

            int tmp = -1;

            try {
                tmp = Integer.parseInt(c);
            } catch (NumberFormatException e) {
                System.out.println(String.format("Enter list of numbers 1 to %d or 'table' to discard.", this.tableCards.size()));
                return Move.INVALID;
            }

            if (tmp < 0) {
                System.out.println(String.format("Not a valid card location. Valid entry is 1 to %d", this.tableCards.size()));
                return Move.INVALID;
            } else if (tmp > this.tableCards.size() -1) {
                System.out.println(String.format("Table does not contain that card. Valid entry is 1 to %d", this.tableCards.size()));
                return Move.INVALID;
            }

            pickup.addCardToPickUp(this.tableCards.get(tmp));
        }
        return handlePickup(pickup);
    }

    public Move handlePickup(Pickup pickup){

            int sum = 0;

        if(pickup.getTableCards().size() != 1) {
            for (Card C : tableCards) {
                if (C.getVal() == pickup.getPlayerCard().getVal()) {
                    System.out.println("You must take the single card for that card. Please try again: ");
                    return Move.INVALID;
                }
            }
        }
            for(int i = 0; i < pickup.getTableCards().size(); i++) {
                sum = sum + (pickup.getTableCards().get(i).getVal());
            }
            if(sum == pickup.getPlayerCard().getVal()){
                currentPlayer.play(Optional.of(pickup.getPlayerCard()), pickup.getTableCards());
                for (Card c: pickup.getTableCards()){
                    tableCards.remove(c);
                }
                if(currentPlayer == player1)
                    lastTrick = 1;
                else{
                    lastTrick = 2;
                }
                return pickup;
            }

            System.out.println("Those do not add up! Please try again: ");
            return Move.INVALID;

    }
    public Move handleDiscard(Discard discard){

        List<Card> sorted = this.tableCards.stream().sorted().collect(Collectors.toList());
        int maxSet = findMaxSetSize(sorted, discard.getDiscarded().getVal());

        for(int i = 1; i <= maxSet; i++){
            List<Set<Card>> s = computeLegalMoves(sorted, i, discard.getDiscarded().getVal());
            if(s.size() > 0){
                System.out.println("You can not discard that card because you can take a trick with it! Please try again.");
                return Move.INVALID;
            }
        }
        currentPlayer.getHand().remove(discard.getDiscarded());
        tableCards.add(discard.getDiscarded());
        return discard;

    }
    public void scoring(Player p){
        if(p.getTotal() > 20){
            p.setScore(p.getScore() + 1);
        }
        if(p.getCoins() > 5){
            p.setScore(p.getScore() + 1);
        }
        if(p.isSevenCoins()){
            p.setScore(p.getScore() + 1);
        }
        p.clearScore();

    }
    public boolean winner(){
        if(player1.getScore() == player2.getScore()){
            return false;
        }
        if(player1.getScore() >= 11 || player2.getScore() >= 11){
            return true;
        }
        else{
            return false;
        }
    }

    List<Set<Card>> computeLegalMoves(List<Card> table, int size, int sumToMatch) {

        int[] counters = new int[size];
        List<Set<Card>> allSets = new ArrayList<>();


        processLoopsRecursively(table, counters, 0,  allSets, sumToMatch);
        return allSets;
    }
    int findMaxSetSize(List<Card> table, int maxSum) {
        int sum = 0;
        int maxSize = 1;
        for (int i = 0; i < table.size(); i++) {
            sum+=table.get(i).getVal();
            if (sum > maxSum) {
                break;
            }
            maxSize++;
        }
        return maxSize;
    }
    void processLoopsRecursively(List<Card> a, int[] counters, int level, List<Set<Card>> allSets, Integer sumToMatch) {
        if(level == counters.length) performSumCheck(a, counters, allSets, sumToMatch);
        else {
            // Optimization to skip duplication.
            int startVal = 0;
            if (level > 0)
                startVal = counters[level-1]+1;

            for (counters[level] = startVal; counters[level] < a.size(); counters[level]++) {
                processLoopsRecursively( a, counters, level + 1, allSets, sumToMatch);
            }
        }
    }

    void performSumCheck(List<Card> a, int[] counters, List<Set<Card>> allSets, Integer sumToMatch) {
        Set<Card> solution = new HashSet<>();
        int sum = 0;
        for (int level = 0; level < counters.length; level++) {

            // Short circuit loops if the exceeds the match value.
            sum += a.get(counters[level]).getVal();
            if (sum > sumToMatch) {
                break;
            }
            solution.add(a.get(counters[level]));
        }
        // Add to the solution only if the sum matches.
        // Using Set to eliminate any dupicates.
        if (solution.size() == counters.length && solution.stream().mapToInt(c -> c.getVal()).sum() == sumToMatch) {
            allSets.add(solution);
        }
    }
    public void player1First(int num){
        if(num % 2 == 0){
            currentPlayer = player1;
            table.addPlayers(player1, player2);
            table.makeHand(num, currentPlayer.getHand());
            table.makeTable(tableCards);
            table.remainingDeck("" + deck.size());
            table.render();
        }
        else{
            currentPlayer = player2;
            table.addPlayers(player2, player1);
            table.makeHand(num, currentPlayer.getHand());
            table.makeTable(tableCards);
            table.remainingDeck("" + deck.size());
            table.render();
        }
    }
    public void player2First(int num){
        if(num % 2 != 0){
            currentPlayer = player1;
            table.addPlayers(player1, player2);
            table.makeHand(num, currentPlayer.getHand());
            table.makeTable(tableCards);
            table.remainingDeck("" + deck.size());
            table.render();
        }
        else{
            currentPlayer = player2;
            table.addPlayers(player2, player1);
            table.makeHand(num, currentPlayer.getHand());
            table.makeTable(tableCards);
            table.remainingDeck("" + deck.size());
            table.render();
        }
    }
    public void instructions(){
        System.out.println("Welcome to Scopa! On the scopa board, each card has a number next to it that corresponds to its position.");
        System.out.println("When taking your turn, use these numbers to indicate the cards you wish to play.");
        System.out.println("When playing, you will be prompted to (Play:). Here you will enter the position number of the card in your hand you wish to play.");
        System.out.println("You will then be prompted (For:). Here you will enter the position number of the cards on the table you wish to pickup or enter (table) to discard.");
    }
    public String nameCheck(String name){
        while(name.length() > 10){
            System.out.println("Your name must be 10 or less characters. Please try again: ");
            name = input.nextLine();
        }
        return name;
    }
}