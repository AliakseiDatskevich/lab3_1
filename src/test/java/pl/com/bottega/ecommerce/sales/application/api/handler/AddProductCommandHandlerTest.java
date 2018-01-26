package pl.com.bottega.ecommerce.sales.application.api.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerTest {

	private ReservationRepository mockedReservationRepository;
	private ProductRepository mockedProductRepository;
	private Reservation reservation;
	private Reservation spy;
	private Product product;
	private AddProductCommandHandler handler;
	private AddProductCommand productCommand;
	private ClientRepository mockedClientRepository;
	private Client client;
	private SuggestionService mockedSuggestionService;
	private Product equivalent;
	private SystemContext systemContext;

	@Before
	public void setUp() {
		handler = new AddProductCommandHandler();
		productCommand = new AddProductCommand(new Id("1"), new Id("2"), 3);
		mockedReservationRepository = mock(ReservationRepository.class);
		mockedProductRepository = mock(ProductRepository.class);
		mockedClientRepository = mock(ClientRepository.class);
		mockedSuggestionService = mock(SuggestionService.class);
		reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
				new ClientData(Id.generate(), "Client"), new Date());
		product = spy(new Product(Id.generate(), new Money(10), "Product", ProductType.STANDARD));
		equivalent = new Product(Id.generate(), new Money(20), "Equivalent", ProductType.STANDARD);
		spy = spy(reservation);
		systemContext = new SystemContext();
		Whitebox.setInternalState(handler, "reservationRepository", mockedReservationRepository);
		Whitebox.setInternalState(handler, "productRepository", mockedProductRepository);
		Whitebox.setInternalState(handler, "suggestionService", mockedSuggestionService);
		Whitebox.setInternalState(handler, "clientRepository", mockedClientRepository);
		Whitebox.setInternalState(handler, "systemContext", systemContext);
		client = new Client();
	}

	@Test
	public void addMethodShouldBeCalledOnce() {
		when(mockedProductRepository.load(any(Id.class))).thenReturn(product);
		when(mockedReservationRepository.load(any(Id.class))).thenReturn(spy);

		handler.handle(productCommand);

		verify(spy, times(1)).add(product, 3);
	}

	@Test
	public void saveMethodShouldBeCalledOnce() {
		when(mockedProductRepository.load(any(Id.class))).thenReturn(product);
		when(mockedReservationRepository.load(any(Id.class))).thenReturn(spy);

		handler.handle(productCommand);

		verify(mockedReservationRepository, times(1)).save(any(Reservation.class));
	}

	@Test
	public void suggestEquivalentMethodShouldBeCalledWhenProductIsNotAvailable() {
		when(mockedClientRepository.load(new Id("1"))).thenReturn(client);
		when(mockedReservationRepository.load(productCommand.getOrderId())).thenReturn(reservation);
		when(mockedProductRepository.load(productCommand.getProductId())).thenReturn(product);
		when(mockedSuggestionService.suggestEquivalent(product, client)).thenReturn(equivalent);
		when(product.isAvailable()).thenReturn(false);

		handler.handle(productCommand);

		verify(mockedSuggestionService).suggestEquivalent(product, client);
	}
}
