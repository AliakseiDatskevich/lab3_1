package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

public class ProductDataBuilder {

    private Id productId = new Id("1");
    private Money price = new Money(0);
    private String name = "name";
    private Date snapshotDate = new Date();
    private ProductType type = ProductType.STANDARD;

    public ProductDataBuilder withId(Id productId){
        this.productId = productId;
        return this;
    }

    public ProductDataBuilder withPrice(Money price){
        this.price = price;
        return this;
    }
    public ProductDataBuilder withName(String name){
        this.name = name;
        return this;
    }
    public ProductDataBuilder withSnapshotData(Date date){
        this.snapshotDate = date;
        return this;
    }
    public ProductDataBuilder withType(ProductType type){
        this.type = type;
        return this;
    }
    public static ProductDataBuilder productData() {
        return new ProductDataBuilder();
    }
    public ProductData build() {
        return new ProductData(productId,price , name, type, snapshotDate);
    }

}
