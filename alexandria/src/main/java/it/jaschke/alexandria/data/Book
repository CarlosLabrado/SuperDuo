package it.jaschke.alexandria.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A book object
 */
public class Book implements Parcelable {

    private String title;
    private String subTitle;
    private String authors;
    private String imgUrl;
    private String categories;

    public Book(String title, String subTitle, String authors, String imgUrl, String categories) {
        this.title = title;
        this.subTitle = subTitle;
        this.authors = authors;
        this.imgUrl = imgUrl;
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    protected Book(Parcel in) {
        title = in.readString();
        subTitle = in.readString();
        authors = in.readString();
        imgUrl = in.readString();
        categories = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(authors);
        dest.writeString(imgUrl);
        dest.writeString(categories);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
