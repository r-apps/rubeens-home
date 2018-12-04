package me.rubeen.apps.android.rubeenshome;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getNetworkDevices() {
        int timeout = 40;
        List<String> list = new LinkedList<>();
        for (int i = 0; i < 255; i++) {
            String host = "192.168.178" + "." + i;
            System.out.println("checking " + host);
            try {
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    list.add(InetAddress.getByName(host).getHostName() + " (" + InetAddress.getByName(host).getCanonicalHostName() + ")");
                    System.out.println("adding " + host);
                }
            } catch (IOException e) {
                System.err.println("error");
            }
        }
        list.forEach(s -> System.out.println(s + " is reachable."));
    }
}