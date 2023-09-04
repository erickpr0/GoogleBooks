package com.example.googlebooks.model.classes;


public class BookDetails {
    private String id;

    private VolumeInfo volumeInfo;

    public String getId() {
        return id;
    }

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    public static class VolumeInfo {
        private String title;

        private String[] authors;

        private String description;

        public String getTitle() {
            return title;
        }
        public String[] getAuthors() {
            return authors;
        }

        public String getDescription() {
            return description;
        }
    }
}
