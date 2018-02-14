package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

public class BookKeeperTest {

    private Money money;
    private Id id;
    private String productName;
    private String description;
    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private ProductData productData;
    private TaxPolicy taxPolicy;
    private RequestItem requestItem;
    private int quantity;

    @Before
    public void initialize() {
        money = new Money(12, "USD");
        id = new Id("12");
        productName = "Onion";
        description = "VEGE";
        quantity = 2;

        bookKeeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = Mockito.mock(InvoiceRequest.class);
        productData = new ProductData(id, money, productName, ProductType.FOOD, new Date());
        requestItem = new RequestItem(productData, quantity, money);
        taxPolicy = Mockito.mock(TaxPolicy.class);
    }

    @Test
    public void issuanceRequestForInvoiceWithOnePosition() {
        int expectedSize = 1;
        List<RequestItem> requestItems = new ArrayList<>();
        requestItems.add(requestItem);

        Mockito.when(invoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getItems().size(), is(expectedSize));

    }

    @Test
    public void issuanceRequestForInvoiceWithTwoPositionCallCalculateTaxTwice() {
        List<RequestItem> requestItems = new ArrayList<>();
        requestItems.add(requestItem);
        requestItems.add(requestItem);

        Mockito.when(invoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, Mockito.times(2))
                .calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void issuanceRequestForInvoiceCheckQuantityOfFirstPosition() {
        List<RequestItem> requestItems = new ArrayList<>();
        requestItems.add(requestItem);

        Mockito.when(invoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getItems().get(0).getQuantity(), is(quantity));
    }

}
