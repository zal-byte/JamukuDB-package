package ModelView;

public class CommentProductModel {
    public String ProdID, KID, PUsername, PName, PProfilePicture, KMessage, KDate;

    public String getProdID() {
        return ProdID;
    }

    public void setProdID(String prodID) {
        ProdID = prodID;
    }

    public String getKID() {
        return KID;
    }

    public void setKID(String KID) {
        this.KID = KID;
    }

    public String getPUsername() {
        return PUsername;
    }

    public void setPUsername(String PUsername) {
        this.PUsername = PUsername;
    }

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getPProfilePicture() {
        return PProfilePicture;
    }

    public void setPProfilePicture(String PProfilePicture) {
        this.PProfilePicture = PProfilePicture;
    }

    public String getKMessage() {
        return KMessage;
    }

    public void setKMessage(String KMessage) {
        this.KMessage = KMessage;
    }

    public String getKDate() {
        return KDate;
    }

    public void setKDate(String KDate) {
        this.KDate = KDate;
    }
}
