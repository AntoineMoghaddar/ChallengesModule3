package Week3;

import java.util.Random;

/**
 * @author Baris Imre and Antoine Moghaddar
 *         Date of creation 02/03/2017.
 */
public class Alt1 implements IMACProtocol {
    private boolean transmitted = false;
    private boolean hasKey = false;
    private boolean someoneHasKey = false;
    private int cycleCounter = 0;
    private int oddIncrease = 0;
    private TransmissionInfo info = new TransmissionInfo(TransmissionType.Silent, 0);

    @Override
    public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
                                              int controlInformation, int localQueueLength){

        System.out.println("Previous state is : " + previousMediumState);
        if (previousMediumState.equals(MediumState.Succes) || previousMediumState.equals(MediumState.Collision)) {
            oddIncrease = 0;
        }
        if (transmitted && previousMediumState.equals(MediumState.Succes)) {
            someoneHasKey = true;
            hasKey = true;
        } else if (!transmitted && previousMediumState.equals(MediumState.Succes)) {
            someoneHasKey = true;
            hasKey = false;
        } else if (previousMediumState.equals(MediumState.Idle) && oddIncrease < 90) {
            someoneHasKey = false;
            hasKey = false;
            oddIncrease += 14;
        }


        // lets find if someone has the key
        if (someoneHasKey) {
            //someone else has the key
            if (!hasKey) {
                if (previousMediumState.equals(MediumState.Idle)) {
                    someoneHasKey = false;
                    hasKey = false;
                    cycleCounter = 0;
                    return silent();
                } else if (previousMediumState.equals(MediumState.Succes)) {
                    if (cycleCounter > 4) {
                        someoneHasKey = false;
                        hasKey = false;
                        cycleCounter = 0;
                        return silent();
                    } else {
                        someoneHasKey = true;
                        cycleCounter++;
                        return silent();
                    }
                }
                // you have the key
            } else {
                if (localQueueLength == 0) {
                    hasKey = false;
                    cycleCounter = 0;
                    someoneHasKey = false;
                    return silent();
                } else {
                    if (cycleCounter < 5) {
                        hasKey = true;
                        someoneHasKey = true;
                        cycleCounter++;
                        return transmit(localQueueLength);
                    } else {
                        hasKey = false;
                        someoneHasKey = false;
                        cycleCounter = 0;
                        return silent();
                    }
                }

            }
            // none has key
        } else {
            if (rng() && localQueueLength != 0) {
                return transmit(localQueueLength);
            } else {
                return silent();
            }
        }
        System.out.println("HERE");
        return null;
    }

    private boolean rng() {
        int random = (new Random().nextInt(100));
        return random < 25 + oddIncrease;
    }

    private boolean rngNoInc() {
        int random = (new Random().nextInt(100));
        return random < 25;
    }


    private TransmissionInfo silent() {
        System.out.println("SLOT - Silent");
        transmitted = false;
        return info = new TransmissionInfo(TransmissionType.Silent, 0);

    }

    private TransmissionInfo transmit(int que) {
        System.out.println("local que: " + que + "\n");
        System.out.println("SLOT - Transmitted");
        transmitted = true;
        return info = new TransmissionInfo(TransmissionType.Data, 0);
    }

    private TransmissionInfo noData(int x) {
        System.out.println("SLOT - Not sending data to give room for others.");
        return new TransmissionInfo(TransmissionType.NoData, x);
    }
}