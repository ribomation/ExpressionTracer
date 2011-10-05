package target;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;


/**
 * Application for functional testing.
 */
public class Application {
    public static void main(String[] args) {
        Application app = new Application(args);
        app.run();
    }

    private int     N         = 1000;
    private long    sleepTime = 1000;
    private List    lst       = new ArrayList();
    private Random  r         = new Random();

    public Application(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-n")) {
                N         = Integer.parseInt(args[++i]);
            } else if (arg.startsWith("-t")) {
                sleepTime = Integer.parseInt(args[++i]);
            }
        }
    }

    public void     run() {
        for (int k = 0; k <= N; ++k) {
            compute(lst);
            if (k % 60 == 0) whatever( getNumber() );
        }
    }

    public void     compute(List lst) {
        Integer  num = getNumber();
        lst.add(num);
//        System.out.println("[" + lst.size() + "] num = " + num);
        print("list.size=" + lst.size() + ", num=" + num);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {}
    }

    public void     print(String msg) {
        System.out.println("[target.Application] " + msg);
    }

    public void     whatever(Integer n) {
        System.out.println("[target.Application#whatever()] n=" + n);
    }

    public Integer     getNumber() {
        return new Integer(r.nextInt(1000));
    }
}
