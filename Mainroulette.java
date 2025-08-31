import java.util.*;

class Player {
    private double balance = 10000.00;
    private int winStreak;
    private int lossStreak;
    private double totalWinning;
    private double goal = 1000000.00;
    // private double initialBalance = 10000.00;

    public Player() {

        this.winStreak = 0;
        this.lossStreak = 0;
        this.totalWinning = 0;
    }

    public double getBalance() {
        return balance;
    }

    public void addBalance(double amount) {
        balance += amount;
    }

    public void subtractBalance(double amount) {
        balance -= amount;
    }

    public double getGoal() {
        return goal;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getLossStreak() {
        return lossStreak;
    }

    public double getTotalWinning() {
        return totalWinning;
    }

    public void incWinStreak() {
        winStreak++;
        lossStreak = 0;
    }

    public void incLossStreak() {
        lossStreak++;
        winStreak = 0;
    }

    public void TotalWinning(double payout, double betAmount) {
        totalWinning += (payout - betAmount);
    }
}


class Casino {
    private double bankBalance;

    public Casino() {
        this.bankBalance = 1000000.00;
    }

    public double getBankBalance() {
        return bankBalance;
    }

    public void addToBank(double amount) {
        bankBalance += amount;
    }

    public void deductFromBank(double amount) {
        bankBalance -= amount;
    }
}


class RouletteWheel {
    private Casino casino;
    private Random random = new Random();

    public RouletteWheel(Casino casino) {
        this.casino = casino;
    }

    public void spinAnimation() {
        System.out.print("\n🎲 Spinning the wheel... ");
        for (int i = 0; i < 25; i++) {
            int num = random.nextInt(37);
            System.out.print(num + " ");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int result(Player player, int betType, int guess) {
        double baseOddEvenWinProb = 0.38;
        double baseNumberWinProb = baseOddEvenWinProb / (35.0 / 2.0); 


        if (player.getWinStreak() >= 3) {
            baseOddEvenWinProb -= 0.12;
            baseNumberWinProb -= 0.12 / (35.0 / 2.0);
        }
        if (player.getLossStreak() >= 3) {
            baseOddEvenWinProb += 0.15;
            baseNumberWinProb += 0.15 / (35.0 / 2.0);
        }

        double error = (random.nextDouble() * 0.1) - 0.05;
        baseOddEvenWinProb += error;
        baseNumberWinProb += error / (35.0 / 2.0);

        baseOddEvenWinProb = Math.min(Math.max(baseOddEvenWinProb, 0.05), 0.7);
        baseNumberWinProb = Math.min(Math.max(baseNumberWinProb, 0.005), 0.05);

        if (casino.getBankBalance() < 50000) {
            baseOddEvenWinProb -= 0.20;
            baseNumberWinProb -= 0.20 / (35.0 / 2.0);
        }
        if (player.getBalance() >= 500000) {
            baseOddEvenWinProb -= 0.25;
            baseNumberWinProb -= 0.25 / (35.0 / 2.0);
        }

        boolean shouldWin;
        int finalNumber;

        if (betType == 1) {
            shouldWin = random.nextDouble() < baseNumberWinProb;
            finalNumber = shouldWin ? guess : getDifferentNumber(guess);
        } else if (betType == 2) { 
            shouldWin = random.nextDouble() < baseOddEvenWinProb;
            finalNumber = shouldWin ? getRandomOdd() : getRandomEven();
        } else if (betType == 3) { 
            shouldWin = random.nextDouble() < baseOddEvenWinProb;
            finalNumber = shouldWin ? getRandomEven() : getRandomOdd();
        } else {
            boolean specialWin = random.nextDouble() < 0.0002;
            finalNumber = specialWin ? guess : getDifferentNumber(guess);
        }

        return finalNumber;

    }

    private int getRandomOdd() {
        int num;
        do {
            num = random.nextInt(37);
        } while (num % 2 == 0 || num == 0);
        return num;
    }

    private int getRandomEven() {
        int num;
        do {
            num = random.nextInt(37);
        } while (num % 2 != 0 || num == 0);
        return num;
    }

    private int getDifferentNumber(int num) {
        int result;
        do {
            result = random.nextInt(37);
        } while (result == num);
        return result;
    }
}

class Game {
    private Player player;
    private Casino casino;
    private RouletteWheel roulette;
    private Scanner sc;

    public Game() {
        this.player = new Player();
        this.casino = new Casino();
        this.roulette = new RouletteWheel(this.casino);
        this.sc = new Scanner(System.in);
    }

    private void bonusRound() {
        System.out.println("\n🎁 BONUS ROUND UNLOCKED! Pick a number between 0 and 36.");
        System.out.print("Your number: ");
        int guess = sc.nextInt();
        if (guess < 0 || guess > 36) {
            System.out.println("❌ Invalid number. Bonus round wasted!");
            return;
        }

        roulette.spinAnimation();
        int result = roulette.result(player, 1, guess);
        System.out.println("🎯 Ball landed on: " + result);

        if (result == guess) {
            double bonusPrize = 1000;
            System.out.println("🔥 You hit the bonus! You win $" + bonusPrize);
            player.addBalance(bonusPrize);
            casino.deductFromBank(bonusPrize);
        } else {
            System.out.println("❌ No luck this time. Better luck next bonus round!");
        }

        player.incWinStreak();
    }

    private void resolveBet(int betType, double betAmount, int result, int guess, boolean hasInsurance) {
        boolean won = false;
        double payout = 0;

        if (betType == 1 && result == guess) {
            won = true;
            payout = betAmount * 35;
        } else if (betType == 2 && result % 2 != 0) {
            won = true;
            payout = betAmount * 2;
        } else if (betType == 3 && result % 2 == 0) {
            won = true;
            payout = betAmount * 2;
        } else if (betType == 4 && result == guess) {
            won = true;
            payout = betAmount * 100;
        }

        if (won) {
            System.out.println("🔥 You won $" + payout);
            player.addBalance(payout);
            player.incWinStreak();
            casino.deductFromBank(payout - betAmount);
            encouragePlayer(true);
            player.TotalWinning(payout, betAmount);
        } else {
            if (hasInsurance) {
                System.out.println("Insurance activated! Bet refunded: $" + betAmount);
                player.addBalance(betAmount);
                casino.deductFromBank(betAmount);
            } else {
                System.out.println("\n❌ You lost.");
                casino.addToBank(betAmount);
                player.incLossStreak();
            }
            encouragePlayer(false);
        }
    }

    private void encouragePlayer(boolean won) {
        if (won) {
            System.out.println("🎉 Feeling lucky? Bet big this time!");
        } else {
            int roll = (int) (Math.random() * 3);
            switch (roll) {
                case 0:
                    System.out.println("💪 Don't give up — your big win is coming!");
                    break;
                case 1:
                    System.out.println("✨ Luck changes in a blink. Bet again!");
                    break;
                case 2:
                    System.out.println("🔥 House wins for now — but fortune favors the bold.");
                    break;
            }
        }
    }

    public void start() {
        for (int i = 1; i <= 130; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.println("🎰 Welcome to Roulette!");
        System.out.println("Goal: Take your $10,000 to $1,000,000+ to become a high roller 🎢!");
        System.out
                .println("✨Tip: You can opt for an insurance for bet above $500 and make your bet amount 100% secure");
        boolean a = true;
        if (casino.getBankBalance() <= 0) {
            a = false;
            System.out.println("Casino is Broke! You win 👑");
        }

        while (a) {
            for (int i = 1; i <= 130; i++) {
                System.out.print("*");
            }
            if (player.getBalance() <= 0) {
                System.out.println();
                System.out.println("You are broke! You lose😂");
                break;
            }
            System.out.println("\n💰 Your Balance: $" + player.getBalance());
            System.out.println("🏦 Casino Balance: $" + casino.getBankBalance());

            if (player.getLossStreak() == 5) {
                System.out.println("\n🎉🎰 *** JACKPOT BONUS ROUND UNLOCKED! *** 🎰🎉");
                System.out.println("💥 5 losses in a row? Time to turn the tables!");
                System.out.println("✨ Pick ANY number between 0 and 36.");
                System.out.println("💸 If your number hits, you WIN 100x your bet instantly!");
                System.out.println("🔥 This is your comeback moment — fortune favors the bold!");
                bonusRound();

            }

            System.out.print("Enter your bet amount: ");
            double betAmount = sc.nextDouble();
            if (betAmount > player.getBalance()) {
                System.out.println("❌ Not enough balance.");
                continue;
            } else {
                player.subtractBalance(betAmount);
            }

            System.out.print("Choose bet type (1 for Number / 2 for Odd / 3 for Even): ");
            int betType = sc.nextInt();
            int guess = 0;
            boolean hasInsurance = false;
            if (betAmount >= 500) {
                System.out.print("Do you want insurance for 50% of your bet amount? (yes/no): ");
                String insChoice = sc.next();
                hasInsurance = insChoice.equalsIgnoreCase("yes");
                double insuranceCost = hasInsurance ? betAmount * 0.5 : 0;

                if (hasInsurance && player.getBalance() < insuranceCost) {
                    System.out.println("Insufficient funds for insurance.");
                    hasInsurance = false;
                } else if (hasInsurance) {
                    player.subtractBalance(insuranceCost);
                    casino.addToBank(insuranceCost);
                }
            }

            if (betType == 1) {
                System.out.print("Pick a number (0-36): ");
                guess = sc.nextInt();
                if (guess < 0 || guess > 36) {
                    System.out.println("❌ Invalid number.");
                    continue;
                }
            }

            roulette.spinAnimation();
            System.out.print(roulette.result(player, betType, guess));
            int result = roulette.result(player, betType, guess);
            System.out.println("\n🎯 Ball landed on: " + result);

            resolveBet(betType, betAmount, result, guess, hasInsurance);

            if (player.getBalance() >= 1_000_000) {
                System.out.println("🎉 YOU'VE MADE IT TO A MILLION! You're a legend!");
                break;
            }
            System.out.println("\n💰 Total winnings: $" + player.getTotalWinning());
            System.out.println("\n💰 Your Balance: $" + player.getBalance());
            System.out.print("\nDo you want to continue betting? (yes/no): ");
            String cont = sc.next();
            if (!cont.equalsIgnoreCase("yes")) {
                break;
            }

        }

        System.out.println("📝 Game ended. Your Final balance: $" + player.getBalance());
        System.out.println("Casino bank balance: $" + casino.getBankBalance());
    }
}

class Mainroulette {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
