package com.mycompany.app;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.mycompany.assembler.RiscVAssembler;
import com.mycompany.data.Section;
import com.mycompany.linkerscriptparser.LinkerScriptParser;
import com.mycompany.preprocessing.IncludePreprocessor;

/**
 * Created with:
 *
 * <pre>
 * mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
 * </pre>
 *
 * Antlr extensions:
 *
 * open a .g4 file > CTRL + SHIFT + P > antlr.call-graph
 *
 * Clean java language server workspace:
 * Ctrl + Shift + P > Clean java language server workspace > Clean and Restart
 */
public class App {

    public static void main(String[] args) throws IOException {

        // create build folder
        Files.createDirectories(Paths.get("build"));

        //
        // preprocess
        //

        // the first step is always to let the preprocessor resolve .include
        // instructions. Let the compiler run on the combined file in a second step!

        //String inputFile = "src/test/resources/projects/snake/Main.asm";
        String inputFile = "src/test/resources/riscvasm/test.s";
        String outputFile = "build/preprocessed.s";

        preprocess(inputFile, outputFile);

        //
        // linker script
        //

        Map<String, Section> sectionMap = new HashMap<>();

        LinkerScriptParser linkerScriptParser = new LinkerScriptParser();
        linkerScriptParser.parseLinkerScript(sectionMap);

        //
        // assemble
        //

        String asmInputFile = "build/preprocessed.s";

        RiscVAssembler riscVAssembler = new RiscVAssembler();
        riscVAssembler.assemble(sectionMap, asmInputFile);

        System.out.println("done");
    }

    private static void preprocess(final String inputFile, final String outputFile) throws FileNotFoundException, IOException {
        
        System.out.println("Precprocessing input file ...");

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {
            
            IncludePreprocessor includePreprocessor = new IncludePreprocessor();
            includePreprocessor.preprocess(inputFile, bufferedWriter);
            
            bufferedWriter.flush();
        }

        System.out.println("Precprocessing input done ...");
    }
}
