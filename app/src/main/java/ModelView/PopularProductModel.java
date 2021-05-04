package ModelView;

public class PopularProductModel {
    public String PID;
    public String ProdName;
    public String ProdDesc;
    public String ProdPrice;
    public String ProdPict;
    public String ProdComm;
    public String ProdLove;

    public String getProdComm() {
        return ProdComm;
    }

    public void setProdComm(String prodComm) {
        ProdComm = prodComm;
    }

    public String getProdLove() {
        return ProdLove;
    }

    public void setProdLove(String prodLove) {
        ProdLove = prodLove;
    }

    public String getProdQuantity() {
        return ProdQuantity;
    }

    public void setProdQuantity(String prodQuantity) {
        ProdQuantity = prodQuantity;
    }

    public String ProdQuantity;

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getProdName() {
        return ProdName;
    }

    public void setProdName(String prodName) {
        ProdName = prodName;
    }

    public String getProdDesc() {
        return ProdDesc;
    }

    public void setProdDesc(String prodDesc) {
        ProdDesc = prodDesc;
    }

    public String getProdPrice() {
        return ProdPrice;
    }

    public void setProdPrice(String prodPrice) {
        ProdPrice = prodPrice;
    }

    public String getProdPict() {
        return ProdPict;
    }

    public void setProdPict(String prodPict) {
        ProdPict = prodPict;
    }
}
