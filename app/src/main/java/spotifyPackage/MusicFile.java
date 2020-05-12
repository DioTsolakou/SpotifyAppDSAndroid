package spotifyPackage;

public class MusicFile implements java.io.Serializable
{
    String title;
    String artistName;
    String albumInfo;
    String genre;
    byte[] musicFileExtract;


    public MusicFile(String t, String aN, String aI, String g, byte[] mfe){
        title = t;
        artistName = aN;
        albumInfo = aI;
        genre = g;
        musicFileExtract = mfe;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public byte[] getMusic() {
        return musicFileExtract;
    }
}
