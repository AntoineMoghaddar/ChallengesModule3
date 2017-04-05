package Week7.src.Location;

import Week7.src.GUI.Viewer;
import Week7.src.Utils.MacRssiPair;
import Week7.src.Utils.Position;
import Week7.src.Utils.Utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Moose.
 */
public class AdvancedLocationFinder implements LocationFinder {
    private HashMap<String, Position> valid; //Contains the known locations of APs. The long is a MAC address.
    private HashMap<String, Integer> routerMap;
    private Position myDeltaPos;
    private Position dDeltaPos;
    private String atRouter;

    public AdvancedLocationFinder() {
        routerMap = new HashMap<>();
        atRouter = "";
        myDeltaPos = new Position(0, 0);
        valid = Utils.getKnownLocations(); //Put the known locations in our hashMap
    }

    @Override
    public Position locate(MacRssiPair[] data) {
        printData(data);
        return best(data);
    }

    private double calculateDistance(int signalLevel) {
        double exp = ((20 * Math.log10(2400)) + Math.abs(signalLevel));
        double c = Math.pow(10.0, exp);
        c *= c;
        c /= 3 * 3;
        return Math.sqrt(c) * 1.5;
    }


    private void printData(MacRssiPair[] data) {
        for (MacRssiPair pair : data) {
            System.out.println(pair);
        }
    }

    private void drawWifiSpots(MacRssiPair[] data, int count) {
        HashSet<int[]> dataSet = new HashSet<int[]>();
        for (int i = 0; i < data.length; i++) {
            if (valid.get(data[i].getMacAsString()) == null) {
                continue;
            }

            int[] array = new int[6];
            Position pos = valid.get(data[i].getMacAsString());
            array[0] = (int) pos.getX();
            array[1] = (int) pos.getY();
            array[2] = (int) Math.ceil(calculateDistance(data[i].getRssi()));
            array[3] = (count <= 0) ? 255 : 0;
            array[4] = (count > 0) ? 255 : 0;
            array[5] = 0;
            count--;
            dataSet.add(array);
        }
//        view.updatePoints(dataSet);
    }

    public class PairComparator implements Comparator<MacRssiPair> {
        @Override
        public int compare(MacRssiPair t1, MacRssiPair t2) {
            return t2.getRssi() - t1.getRssi();
        }
    }

    private void processData(MacRssiPair pair) {
        String router = pair.getMacAsString();
        if (valid.get(router) != null) {
            Position pos = valid.get(router);
            if (routerMap.containsKey(router)) {
                int diff = Math.abs(routerMap.get(router) - pair.getRssi());
                System.out.println("diff" + diff);
                //TODO finish this
            }
        }
    }


    private Position best(MacRssiPair[] data) {
        {
            Arrays.sort(data, new PairComparator());
            drawWifiSpots(data, 1);
            dDeltaPos = new Position(0, 0);
            double dst = 0;
            for (int i = 0; i < data.length; i++) {
                if (valid.get(data[i].getMacAsString()) == null) {
                    continue;
                }
                if (atRouter.equals("")) {
                    System.out.println("Setting first atRouter");
                    atRouter = data[i].getMacAsString();
                    routerMap.put(atRouter, data[i].getRssi());
                }
                if (data[i].getRssi() > routerMap.get(atRouter)) {
                    Position myPos = valid.get(atRouter);
                    myPos = new Position(myPos.getX() + myDeltaPos.getX(), myPos.getY() + myDeltaPos.getY());
                    Position newPos = valid.get(data[i].getMacAsString());
                    myDeltaPos = new Position(myPos.getX() - newPos.getX(), myPos.getY() - newPos.getY());
                    atRouter = data[i].getMacAsString();
                }
                processData(data[i]);
            }
            myDeltaPos = new Position(myDeltaPos.getX() + dDeltaPos.getX(), myDeltaPos.getY() + dDeltaPos.getY());
            Position myPos = valid.get(atRouter);
            myPos = new Position(myPos.getX() + myDeltaPos.getX(), myPos.getY() + myDeltaPos.getY());
            return myPos;
        }
    }
}