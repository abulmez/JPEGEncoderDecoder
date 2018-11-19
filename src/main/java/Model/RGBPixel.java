package Model;

public class RGBPixel {

    private Integer red,green,blue;

    public RGBPixel(Integer red, Integer green, Integer blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGBPixel(){
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public Integer getRed() {
        return red;
    }

    public Integer getGreen() {
        return green;
    }

    public Integer getBlue() {
        return blue;
    }

    public void setRed(Integer red) {
        this.red = red;
    }

    public void setGreen(Integer green) {
        this.green = green;
    }

    public void setBlue(Integer blue) {
        this.blue = blue;
    }
}
