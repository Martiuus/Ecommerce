// Ruta: final-java-shoes/src/main/java/idat/pe/final_java_shoes/service/PedidoService.java

package idat.pe.final_java_shoes.service;

import idat.pe.final_java_shoes.model.Pedido;
import idat.pe.final_java_shoes.model.Usuario;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public interface PedidoService {
    Pedido crearPedido(Usuario cliente, HttpSession session);
    List<Pedido> findAll();
    void cancelarPedido(Long pedidoId);
}