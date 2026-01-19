package com.example.demo.audit;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;

@Service
public class AuditLogService {

    private final Path dir = Paths.get("logs");

    public AuditLogService() throws Exception {
        Files.createDirectories(dir);
    }

    public synchronized void appendLine(String fileName, String line) {
        try {
            Path p = dir.resolve(fileName);
            String out = Instant.now() + " " + line + System.lineSeparator();
            Files.write(p, out.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            // bewusst keine Exception nach außen: Logging darf die Applikation nicht stoppen
            e.printStackTrace();
        }
    }
}
