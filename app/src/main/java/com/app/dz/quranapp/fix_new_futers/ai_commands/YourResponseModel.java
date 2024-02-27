package com.app.dz.quranapp.fix_new_futers.ai_commands;

import java.util.List;

public class YourResponseModel {
    private String text;
    private List<Intent> intents;
    private Entities entities;
    private Traits traits;

    public String getText() {
        return text;
    }

    public List<Intent> getIntents() {
        return intents;
    }

    public Entities getEntities() {
        return entities;
    }

    public Traits getTraits() {
        return traits;
    }

    public static class Intent {
        private String id;
        private String name;
        private double confidence;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    public static class Entities {
        private List<SuraName> sura_name;

        public List<SuraName> getSura_name() {
            return sura_name;
        }
    }

    public static class SuraName {
        private String id;
        private String name;
        private String role;
        private double start;
        private double end;
        private String body;
        private double confidence;
        private String value;
        private String type;
        private Entities entities;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public double getStart() {
            return start;
        }

        public double getEnd() {
            return end;
        }

        public String getBody() {
            return body;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public Entities getEntities() {
            return entities;
        }
    }

    public static class Traits {
        // You can add traits properties if available in your response
    }

    @Override
    public String toString() {
        return "YourResponseModel{" +
                "text='" + text + '\'' +
                ", intents=" + intents.toString() +
                ", entities=" + entities +
                ", traits=" + traits +
                '}';
    }
}
