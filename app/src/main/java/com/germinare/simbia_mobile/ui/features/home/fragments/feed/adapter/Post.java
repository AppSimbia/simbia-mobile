package com.germinare.simbia_mobile.ui.features.home.fragments.feed.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.germinare.simbia_mobile.data.api.model.postgres.PostResponse;
import com.germinare.simbia_mobile.data.api.model.postgres.ProductCategoryResponse;

import java.util.List;

public class Post implements Parcelable {

    private Long idPost;
    private String title;
    private String description;
    private Double price;
    private Integer quantity;
    private String urlImage;
    private String urlIndustry;
    private String industryName;
    private String industryCnpj;
    private String category;
    private String classification;
    private Integer measureUnit;

    public Post(Long idPost, String title, String description, Double price,
                Integer quantity, String urlImage, String urlIndustry, String category,
                String classification, String industryName, String industryCnpj, Integer measureUnit) {
        this.idPost = idPost;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.urlImage = urlImage;
        this.urlIndustry = urlIndustry;
        this.category = category;
        this.classification = classification;
        this.industryName = industryName;
        this.industryCnpj = industryCnpj;
        this.measureUnit = measureUnit;
    }

    public Post() {}

    public Post(PostResponse response) {
        this.idPost = response.getIdPost();
        this.title = response.getTitle();
        this.description = response.getDescription();
        this.price = response.getPrice();
        this.quantity = response.getQuantity();
        this.urlImage = response.getImage();
        this.urlIndustry = response.getIndustryImage();
        this.category = response.getProductCategory().getCategoryName();
        this.classification = response.getClassification();
        this.industryName = response.getIndustryName();
        this.industryCnpj = response.getIndustryCnpj();
        this.measureUnit = Integer.parseInt(response.getMeasureUnit());
    }

    protected Post(Parcel in) {
        idPost = in.readByte() == 0 ? null : in.readLong();
        title = in.readString();
        description = in.readString();
        price = in.readByte() == 0 ? null : in.readDouble();
        quantity = in.readByte() == 0 ? null : in.readInt();
        urlImage = in.readString();
        urlIndustry = in.readString();
        category = in.readString();
        classification = in.readString();
        industryName = in.readString();
        industryCnpj = in.readString();
        measureUnit = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (idPost == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(idPost);
        }

        dest.writeString(title);
        dest.writeString(description);

        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }

        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }

        dest.writeString(urlImage);
        dest.writeString(urlIndustry);
        dest.writeString(category);
        dest.writeString(classification);
        dest.writeString(industryName);
        dest.writeString(industryCnpj);
        dest.writeInt(measureUnit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    // Getters
    public Long getIdPost() {
        return idPost;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public String getUrlIndustry() {
        return urlIndustry;
    }

    public String getCategory() {
        return category;
    }

    public String getClassification() {
        return classification;
    }

    public String getIndustryName() {
        return industryName;
    }

    public String getIndustryCnpj() {
        return industryCnpj;
    }

    public Integer getMeasureUnit() {
        return measureUnit;
    }

    @Override
    public String toString() {
        return "Post{" +
                "idPost=" + idPost +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", urlImage='" + urlImage + '\'' +
                ", urlIndustry='" + urlIndustry + '\'' +
                ", industryName='" + industryName + '\'' +
                ", industryCnpj='" + industryCnpj + '\'' +
                ", category='" + category + '\'' +
                ", classification='" + classification + '\'' +
                ", measureUnit=" + measureUnit +
                '}';
    }
}
