package Model;

public class YUVPixel {

    private Integer luminance,blueChrominance,redChrominance;

    public YUVPixel() {
        this.luminance = 0;
        this.blueChrominance = 0;
        this.redChrominance = 0;
    }

    public YUVPixel(Integer luminance, Integer blueChrominance, Integer redChrominance) {
        this.luminance = luminance;
        this.blueChrominance = blueChrominance;
        this.redChrominance = redChrominance;
    }

    public void setLuminance(Integer luminance) {
        this.luminance = luminance;
    }

    public void setBlueChrominance(Integer blueChrominance) {
        this.blueChrominance = blueChrominance;
    }

    public void setRedChrominance(Integer redChrominance) {
        this.redChrominance = redChrominance;
    }

    public Integer getLuminance() {
        return luminance;
    }

    public Integer getBlueChrominance() {
        return blueChrominance;
    }

    public Integer getRedChrominance() {
        return redChrominance;
    }
}
