public class Player {
    private String nickname;
    private int bestScore;
    private int numberOfWins;
    private int numberOfLoses;
    private int tries;
    private boolean isLeader;
    private boolean isMaster;


    public Player(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }

    public int getNumberOfLoses() {
        return numberOfLoses;
    }

    public void setNumberOfLoses(int numberOfLoses) {
        this.numberOfLoses = numberOfLoses;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }
}
