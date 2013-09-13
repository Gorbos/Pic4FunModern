/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jpact.pic4funmodern.util;

import java.util.ArrayList;

/**
 *
 * @author Dave
 */

/** Class used in displaying Facebook friends list **/

@SuppressWarnings({ "serial", "rawtypes" })
public class FriendArrayList extends ArrayList {

    public FriendArrayList(int capacity) {
        super(capacity);
    }

    public FriendArrayList() {
    }

    public FriendInterface getObject(int index) {
        return (FriendInterface) super.get(index);
    }

    @SuppressWarnings("unchecked")
	public void add(FriendInterface item) {
        super.add(item);
    }
}

