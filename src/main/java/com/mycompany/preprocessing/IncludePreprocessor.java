package com.mycompany.preprocessing;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IncludePreprocessor {

    private List<String> includedFiles = new ArrayList<>();

    public void preprocess(final String inputFile, final BufferedWriter bufferedWriter)
            throws FileNotFoundException, IOException {

        System.out.println("Trying to include preprocess \"" + inputFile + "\"");
        var inputPath = Paths.get(inputFile);

        if (!Files.exists(inputPath)) {
            throw new RuntimeException("Cannot read file: \"" + inputFile + "\"");
        }

        includedFiles.add(inputFile);
        Path filePath = Paths.get(inputFile);
        Path relativePath = filePath.getParent();

        Files.lines(inputPath)
                .forEach(line -> {
                    try {
                        processLine(line, relativePath, bufferedWriter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }

    private void processLine(final String line, final Path relativePath, final BufferedWriter bufferedWriter)
            throws IOException {

        final String data = line.trim();

        if (data.startsWith(".include")) {

            String split[] = data.split("\s+");

            System.out.println("include found!");

            Path newPath = relativePath.resolve(split[1].substring(1, split[1].length() - 1));

            preprocess(newPath.toAbsolutePath().toString(), bufferedWriter);

        } else {
            bufferedWriter.write(line + "\n");
        }
    }

}
