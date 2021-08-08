package controller;

public class test {

 public static String reverseWords(final String original)
    {
        StringBuilder origString = new StringBuilder();
        origString.append(original);
        return String.valueOf(origString.reverse());
    }
}