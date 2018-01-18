package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Before;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private TaxPolicy taxPolicy;
    private RequestItem requestItem;
    private ProductData productData;
    private Money money;
    private Invoice invoice;
    private Id id;

    @Before
    public void setUp() {
        id = new Id("1234");
        money = new Money(100);
        productData = new ProductData(id, money, "Item", ProductType.FOOD, new Date());
        requestItem = new RequestItem(productData, 10, money);
        bookKeeper = new BookKeeper(new InvoiceFactory());
        taxPolicy = mock(TaxPolicy.class);
        invoiceRequest = mock(InvoiceRequest.class);
    }

}
