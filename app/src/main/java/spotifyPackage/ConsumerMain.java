package spotifyPackage;

import java.util.Scanner;

public class ConsumerMain {
    public static void main(String []args){
        String song;

        Scanner in = new Scanner(System.in);
        System.out.println("Please input your desired song : ");
        song = in.nextLine();

        Consumer phone = new Consumer(song, "");
    }
}