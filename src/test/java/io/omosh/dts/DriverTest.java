package io.omosh.dts;

public class DriverTest {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load MySQL JDBC Driver");
            e.printStackTrace();
        }
        double[] a = {1.1,2.2,3.3};
        int k = 3;
        f(a,k);

        System.out.println(k);
        System.out.println(a[0]);
        System.out.println(a[1]);
        System.out.println(a[2]);

    }

    public static void f(double[] a, int b){

        double x = f(a,b*1.0);
    }


    public static double f(double[] a, double b){
        a[0] = a[0]*b;
        a[1] = a[1]*b;
        a[2] = a[2]*b;
        return a[0]+ a[1] + a[2];
    }
}
