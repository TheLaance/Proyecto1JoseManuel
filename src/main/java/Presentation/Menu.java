package Presentation;

import java.util.Scanner;

public abstract class Menu {
    protected Scanner scanner;

    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    public abstract void display();
    public abstract void handlerInput();

    public void run() {
        display();
        handlerInput();
    }
}
