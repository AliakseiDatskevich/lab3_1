package pl.com.bottega.ecommerce.sales.application.api.handler;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class ProductBuilder {

    private Id id = new Id("1");
    private Money money = new Money(100);
    private String name = "Item";
    private ProductType productType = ProductType.DRUG;

    public Product build(){
        return new Product(id, money, name, productType);
    }

    public ProductBuilder withId(Id id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withMoney(Money money) {
        this.money = money;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }

}
