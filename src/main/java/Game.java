import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import org.sql2o.*;

public class Game {
  private int id;
  private String gameName;
  private String[] suits = {"clubs", "spades", "hearts", "diamonds"};
  private Integer[] ranks = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
  private ArrayList<Card> newDeck = new ArrayList<Card>();
  private ArrayList<Card> hand = new ArrayList<Card>();
  private ArrayList<Card> allPairs = new ArrayList<Card>();
  private ArrayList<Card> exchangeList = new ArrayList<Card>();

  public Game(String gameName) {
    this.gameName = gameName;
  }

  public int getId() {
    return id;
  }

  public String getGameName() {
    return gameName;
  }

  public boolean isEqualTo(Object otherGame) {
    if (!(otherGame instanceof Game)) {
      return false;
    } else {
      Game newGame = (Game) otherGame;
      return this.getGameName().equals(newGame.getGameName());
    }
  }


  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO games (gameName) VALUES (:gameName)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("gameName", this.gameName)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Game> all() {
    String sql = "select * from games";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql)
      .executeAndFetch(Game.class);
    }
  }

  public static Game find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM games WHERE id=:id";
      Game game = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Game.class);
      return game;
    }
  }

  public ArrayList<Card> getAllPairsArray() {
    return allPairs;
  }

  public ArrayList<Card> getDeckCards() {
    return newDeck;
  }

  public ArrayList<Card> getHandCards() {
    return hand;
  }

  public ArrayList<Card> getDeck() {
    int value = 1;
    for (String suit : suits) {
      for(int rank : ranks) {
        String link = "../images/" + value + ".svg";
        Card card = new Card(suit, rank, link);
        value ++;
        newDeck.add(card);
      }
    }
    return newDeck;
  }

  public ArrayList<Card> getHand() {
    for (int i = 0; i < 5; i++) {
      Random random = new Random();
      int randomNumber = random.nextInt(newDeck.size());
      Card chosen = newDeck.get(randomNumber);
      hand.add(chosen);
      newDeck.remove(chosen);
    }
    return hand;
  }

  public void exchangeCards(ArrayList<Card> hand, ArrayList<Card> exchangeList) {
    for (int i = 0; i < exchangeList.size(); i++) {
      Card exchangeCard = exchangeList.get(i);
      hand.remove(exchangeCard);
      Random random = new Random();
      int randomNumber = random.nextInt(newDeck.size());
      Card chosen = newDeck.get(randomNumber);
      hand.add(chosen);
      newDeck.remove(chosen);
    }
  }

  public Card getHighCard(ArrayList<Card> hand) {
    int highest = hand.get(0).getRank();
    Card highestCard = hand.get(0);

    for(int i=1; i<hand.size(); i++) {
      if(hand.get(i).getRank() > highest) {
        highest = hand.get(i).getRank();
        highestCard = hand.get(i);
      }
    } return highestCard;
  }

  public Card getLowCard(ArrayList<Card> hand) {
    int lowest = hand.get(0).getRank();
    Card lowestCard = hand.get(0);

    for(int i=1; i<hand.size(); i++) {
      if(hand.get(i).getRank() < lowest) {
        lowest = hand.get(i).getRank();
        lowestCard = hand.get(i);
      }
    } return lowestCard;
  }

  public void getAllPairs(ArrayList<Card> hand) {
    Card card1 = hand.get(0);
    Card card2 = hand.get(1);
    Card card3 = hand.get(2);
    Card card4 = hand.get(3);
    Card card5 = hand.get(4);

    for (int i = 0; i < 5; i++) {
      if ( (card1.getRank() == hand.get(i).getRank()) && (!(card1.isSameCard(hand.get(i))))) {
        if (!(this.isInPairsArray(card1, allPairs))) {
          allPairs.add(card1);
        }
      }

      if ( (card2.getRank() == hand.get(i).getRank()) && (!(card2.isSameCard(hand.get(i))))) {
        if (!(this.isInPairsArray(card2, allPairs))) {
          allPairs.add(card2);
        }
      }

      if ( (card3.getRank() == hand.get(i).getRank()) && (!(card3.isSameCard(hand.get(i))))) {
        if (!(this.isInPairsArray(card3, allPairs))) {
          allPairs.add(card3);
        }
      }

      if ( (card4.getRank() == hand.get(i).getRank()) && (!(card4.isSameCard(hand.get(i))))) {
        if (!(this.isInPairsArray(card4, allPairs))) {
          allPairs.add(card4);
        }
      }

      if ( (card5.getRank() == hand.get(i).getRank()) && (!(card5.isSameCard(hand.get(i))))) {
        if (!(this.isInPairsArray(card5, allPairs))) {
          allPairs.add(card5);
        }
      }
    }
  }

  public boolean isInPairsArray(Card card, ArrayList<Card> allPairs) {
    boolean isPair = false;
    for (int i = 0; i < allPairs.size(); i++){
      if (card.isSameCard(allPairs.get(i))) {
        isPair = true;
      }
    }
    return isPair;
  }

  // Start of winning conditions.
  public boolean isFlush(ArrayList<Card> hand) {
    String handSuit = hand.get(0).getSuit();

    if (hand.get(1).getSuit().equals(handSuit) && hand.get(2).getSuit().equals(handSuit) && hand.get(3).getSuit().equals(handSuit) && hand.get(4).getSuit().equals(handSuit) ) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isPair(ArrayList<Card> allPairs) {
    if (allPairs.size() == 2) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isStraight(ArrayList<Card> hand) {
    boolean straight = false;
    this.getAllPairs(hand);
    if((allPairs.size() == 0) && (this.getHighCard(hand).getRank() - this.getLowCard(hand).getRank() == 4)){
      straight = true;
    } else {
      straight = false;
    }
    return straight;
  }

  public boolean isRoyalFlush(ArrayList<Card> hand) {
    if((!(this.isPair(hand))) && (this.getLowCard(hand).getRank() == 10) && this.isFlush(hand)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isStraightFlush(ArrayList<Card> hand) {
    if(this.isStraight(hand) && this.isFlush(hand)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isFacePair() {
    if (allPairs.size() == 2 && allPairs.get(0).getRank() > 10) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isThreeOfAKind() {
    if (allPairs.size() == 3) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isFullHouse() {
    if (allPairs.size() == 5) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isFourOfAKind() {
    if (allPairs.size() == 4 && allPairs.get(0).getRank() == allPairs.get(1).getRank() && allPairs.get(0).getRank() == allPairs.get(2).getRank()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isTwoPair() {
    if (allPairs.size() == 4 && this.isFourOfAKind() == false) {
      return true;
    } else {
      return false;
    }
  }

  public int updateScore(Player player, ArrayList<Card> hand) {
    int multiplier = 0;
    int bet = player.getBet();
    Integer score = player.getScore();
    if (this.isRoyalFlush(hand)) {
      multiplier = 250;
    } else if (this.isStraightFlush(hand)) {
      multiplier = 50;
    } else if (this.isFourOfAKind()) {
      multiplier = 25;
    } else if (this.isFullHouse()) {
      multiplier = 9;
    } else if (this.isFlush(hand)) {
      multiplier = 6;
    } else if (this.isStraight(hand)) {
      multiplier = 4;
    } else if (this.isThreeOfAKind()) {
      multiplier = 3;
    } else if (this.isTwoPair()) {
      multiplier = 2;
    } else if (this.isFacePair()) {
      multiplier = 1;
    }

    if (multiplier == 0) {
      score -= bet;
    } else if (multiplier > 0) {
      score += multiplier * bet;
    }
    player.setScore(score);
    return score;
  }
}
