package model;

/**
 * @Author You Jia
 * @Date 10/18/2017 11:27 AM
 */
    public class UserInfo {
        private int id;
        private String name;
        private float score;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setScore(float score) {
            this.score = score;
        }

        public float getScore() {
            return score;
        }
    }

