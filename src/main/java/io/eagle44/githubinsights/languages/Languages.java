package io.eagle44.githubinsights.languages;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Languages {
    private final ConcurrentHashMap<String, Long> languages;

    public Languages() {
        languages = new ConcurrentHashMap<>();
    }

    public void addLanguages(Map<String, Long> languagesOfOneRepo) {
        for (String language : languagesOfOneRepo.keySet()) {
            Long currNumOfBytes = Objects.requireNonNullElse(languages.get(language), 0L);
            languages.put(language, currNumOfBytes + languagesOfOneRepo.get(language));
        }
    }

    public HashMap<String, Long> getLanguagesSortedDesc() {
        return languages.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Languages languages1 = (Languages) o;
        return Objects.equals(languages, languages1.languages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languages);
    }
}
