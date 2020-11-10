package space.devport.wertik.treasures.system.treasure.policy;

import space.devport.wertik.treasures.system.treasure.struct.Treasure;

public interface PolicyExecutor {

    /**
     * Execute a policy on treasure.
     *
     * @return True if successful
     */
    boolean execute(Treasure treasure);
}
