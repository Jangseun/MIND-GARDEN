package com.mindgarden.app.garden;

public final class GardenRules {
    public static final String KEYWORD_PASSION = "열정";
    public static final String KEYWORD_CARE = "배려";
    public static final String KEYWORD_CONSISTENCY = "꾸준함";
    public static final String KEYWORD_RESPONSIBILITY = "책임감";

    public static final Structure[] STRUCTURES = {
            new Structure("sign", "나무 이름표", 3, "여기서부터 당신의 이야기가 시작돼요."),
            new Structure("bench", "작은 나무 벤치", 5, "가끔은 걸어온 길을 돌아봐요."),
            new Structure("fence", "하얀 울타리", 10, "생각들이 모여 정원의 모습을 만들었어요."),
            new Structure("lamp", "작은 정원등", 15, "하나의 깨달음이 앞으로의 길을 밝혀줄 거예요."),
            new Structure("fountain", "작은 분수", 25, "정원에 새로운 생기가 흐르기 시작했어요.")
    };

    private GardenRules() { }

    public static int flowerLevel(int count) {
        if (count <= 0) return 0;
        if (count <= 2) return 1;
        if (count <= 5) return 2;
        if (count <= 9) return 3;
        return 4;
    }

    public static final class Structure {
        public final String id;
        public final String name;
        public final int threshold;
        public final String message;

        private Structure(String id, String name, int threshold, String message) {
            this.id = id;
            this.name = name;
            this.threshold = threshold;
            this.message = message;
        }
    }
}
