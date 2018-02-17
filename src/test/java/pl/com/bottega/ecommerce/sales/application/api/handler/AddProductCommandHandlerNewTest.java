package pl.com.bottega.ecommerce.sales.application.api.handler;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;

public class AddProductCommandHandlerNewTest {

	private AddProductCommandHandler handler;

	private ReservationRepository reservationRepository;

	private ProductRepository productRepository;

	private SuggestionService suggestionService;

	private ClientRepository clientRepository;

	private SystemContext systemContext;

	@Before
	public void setUp() {
		handler = new AddProductCommandHandler();
		clientRepository = mock(ClientRepository.class);
		productRepository = mock(ProductRepository.class);
		reservationRepository = mock(ReservationRepository.class);
		suggestionService = mock(SuggestionService.class);
		systemContext = new SystemContext();
		Client client = mock(Client.class);
		doReturn(client).when(clientRepository).load(any(Id.class));

		handler.setClientRepository(clientRepository);
		handler.setProductRepository(productRepository);
		handler.setReservationRepository(reservationRepository);
		handler.setSuggestionService(suggestionService);
		handler.setSystemContext(systemContext);
	}

	@Test
	public void testReservationRepositoryCalledOnce() {
		Id orderId = Id.generate();
		Id productId = Id.generate();
		Product product = mock(Product.class);
		doReturn(true).when(product).isAvailable();
		doReturn(product).when(productRepository).load(productId);
		Reservation reservation = mock(Reservation.class);
		doReturn(reservation).when(reservationRepository).load(orderId);

		handler.handle(new AddProductCommand(orderId, productId, 2));

		verify(handler.getReservationRepository(), times(1)).load(orderId);
	}

	@Test
	public void testProductRepositoryCalledOnce() {
		Id orderId = Id.generate();
		Id productId = Id.generate();
		Product product = mock(Product.class);
		doReturn(true).when(product).isAvailable();
		doReturn(product).when(productRepository).load(productId);
		Reservation reservation = mock(Reservation.class);
		doReturn(reservation).when(reservationRepository).load(orderId);

		handler.handle(new AddProductCommand(orderId, productId, 2));

		verify(handler.getProductRepository(), times(1)).load(productId);
	}

	@Test
	public void testReservationRepositorySavedOnce() {
		Id orderId = Id.generate();
		Id productId = Id.generate();
		Product product = mock(Product.class);
		doReturn(true).when(product).isAvailable();
		doReturn(product).when(productRepository).load(productId);
		Reservation reservation = mock(Reservation.class);
		doReturn(reservation).when(reservationRepository).load(orderId);

		handler.handle(new AddProductCommand(orderId, productId, 2));

		verify(handler.getReservationRepository(), times(1)).save(reservation);
	}

	@Test
	public void testReservationAdded() {
		Id orderId = Id.generate();
		Id productId = Id.generate();
		Product product = mock(Product.class);
		doReturn(true).when(product).isAvailable();
		doReturn(product).when(productRepository).load(productId);
		Reservation reservation = mock(Reservation.class);
		doReturn(reservation).when(reservationRepository).load(orderId);

		handler.handle(new AddProductCommand(orderId, productId, 2));

		verify(reservation, times(1)).add(product, 2);
	}

	@Test
	public void testEquivalentForNonExistingProduct() {
		Id orderId = Id.generate();
		Id productId = Id.generate();
		Product problematicProduct = mock(Product.class);
		doReturn(false).when(problematicProduct).isAvailable();
		doReturn(problematicProduct).when(productRepository).load(productId);
		Product equivalentProduct = mock(Product.class);
		doReturn(equivalentProduct).when(suggestionService).suggestEquivalent(eq(problematicProduct),
				any(Client.class));
		Reservation reservation = mock(Reservation.class);
		doReturn(reservation).when(reservationRepository).load(orderId);

		handler.handle(new AddProductCommand(orderId, productId, 2));

		verify(reservation, times(1)).add(equivalentProduct, 2);
	}

}
