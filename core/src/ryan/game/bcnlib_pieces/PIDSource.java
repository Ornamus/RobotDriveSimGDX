package ryan.game.bcnlib_pieces;

public interface PIDSource {

    /**
     * Gets the value being used for PID.
     *
     * @return The value being used for PID.
     */
    double getForPID();

    /**
     * Resets this PIDSource.
     */
    void reset();
}