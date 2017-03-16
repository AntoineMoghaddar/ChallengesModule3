package Week3;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Baris Imre && Antoine Moghaddar
 *         Date of creation 02/03/2017.
 */
public class OurProtocol implements IMACProtocol {


    private int odd;

    @Override
    public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
                                              int controlInformation, int localQueueLength) {
        if (localQueueLength == 0) {
            System.out.println("SLOT - No data to send.");
            System.out.println("local que: " + localQueueLength + "\n");
            return new TransmissionInfo(TransmissionType.Silent, 0);
        }

        odd = odd(previousMediumState);
        while (previousMediumState.equals(MediumState.Collision)) {
            return (new Random().nextInt(100) < odd) ? new TransmissionInfo(TransmissionType.Data, 0) : new TransmissionInfo(TransmissionType.Silent, 0);
        }
        return null;
    }

    private int odd(MediumState previousMediumState) {
        ArrayList<MediumState> ACKS = new ArrayList<>();

        if (previousMediumState.equals(MediumState.Succes)) {
            ACKS.add(previousMediumState);
            switch (ACKS.size()) {
                /**
                 * A is send
                 * B is not
                 * C  & D are unknown
                 */
                case 1: return 33;
                /**
                 * A & B are send
                 * C is not
                 * D is unknown
                 */
                case 2: return 50;

                /**
                 * A & B & C are send
                 * D is not
                 */
                case 3: return 100;
                /**
                 * A is not send yet
                 * odd = 25
                 */
                default: return 25;
            }
        }
        return 0;
    }
}

