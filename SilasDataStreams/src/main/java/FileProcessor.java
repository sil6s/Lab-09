package main.java;

import java.util.stream.Collectors;

public class FileProcessor {
    public String filterContent(String content, String searchString) {
        return content.lines()
                .filter(line -> line.contains(searchString))
                .collect(Collectors.joining("\n"));
    }
}
// .lines() creates a stream of lines from
// the input string.
//.filter() is a stream operation that
// keeps only the lines containing the search string.