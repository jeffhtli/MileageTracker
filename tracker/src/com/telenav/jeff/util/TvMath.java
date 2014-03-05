package com.telenav.jeff.util;

public class TvMath {
        private static int [] cosineTable = {
                8192, 8172, 8112, 8012,
                7874, 7697, 7483, 7233,
                6947, 6627, 6275, 5892,
                5481, 5043, 4580, 4096,
                3591, 3068, 2531, 1981,
                1422,  856,  285, -285
           };

        /** SCALE */
        private static final short SCALE = 8192;
        /** corresponding SHIFT value */
        private static final byte SHIFT = 13; //log base 2 of SCALE
        
        //--------------------------------------------------------
        // local nav functions
        //--------------------------------------------------------
        /**
         * calculate X * cosine ( Y ) using the approximation table
         * @param x - X value (SCALE)
         * @param y - Y value (angle)
         * @return X * cosine ( Y )
         */
        private static long xCosY(long x, int y){
                //first make 0 < Y < 360
                int absy = Math.abs(y); // cosine is symmetric, avoid negatives
                absy = absy % 360; // just in case if absolute value > 360
                
                if (absy > 270)
                        return xCosY(x, 360 - absy);
                else if (absy > 180)
                        return -xCosY(x, absy - 180);
                else if (absy > 90)
                        return -xCosY(x, 180 - absy);
                else{
                        int index = absy / 4; // index = integer part of value / 4
                        int offset = absy % 4; // y - 4 * index
                        int cosVal = (4 - offset) * cosineTable[index] + offset * cosineTable[index + 1];
                        long rVal = x * (long) cosVal;
                        return rVal >> SHIFT + 2; // normalize
                }
        }

        private static long rss(long a, long b){
                long absa = Math.abs(a);
                long absb = Math.abs(b);
                long sumq;
                long root;
                
                sumq = a * a + b * b;
                
                root = (absa > absb) ? absb / 2 + absa : absa / 2 + absb;
                
                if (root == 0)
                        return 0;
                
                for (int i = 0; i < 4; i++){
                        root += sumq / root;
                        root = root >> 1;
                }
                
                return root;
        }
        
        public static long dist(long lat1, long lon1, long lat2, long lon2) throws Exception{
                long dLat = Math.abs(lat1 - lat2);
                long dLon = Math.abs(lon1 - lon2);
                        
                int alpha = (int) (lat1 / 100000);
                long dLonC = xCosY(dLon, alpha);
                long delta = rss(dLat, dLonC);
                
                return delta;
        }
        
    public static long calcDist(double lat1, double lon1, double lat2, double lon2)
    {
        double R = 6371; // km
        double dlat = Math.toRadians(lat2 - lat1);
        double dlong = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dlat / 2.0) * Math.sin(dlat / 2.0) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dlong / 2.0) * Math.sin(dlong / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = (R * c);
        return (long) (d * 1000);
    }
}