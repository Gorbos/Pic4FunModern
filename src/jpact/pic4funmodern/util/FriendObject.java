/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jpact.pic4funmodern.util;

/**
 *
 * @author Dave
 */

/** Class used in displaying Facebook friends list **/

public abstract class FriendObject implements FriendInterface {

    public String id;
    public String name;
    public String picture;
    
    public FriendObject() {
        this.id = null;
        this.name = null;
        this.picture = null;
    }

    public FriendObject(String id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }
}