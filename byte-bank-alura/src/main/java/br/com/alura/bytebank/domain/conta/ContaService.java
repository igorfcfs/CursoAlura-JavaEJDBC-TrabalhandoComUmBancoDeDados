package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;
import br.com.alura.bytebank.domain.cliente.Cliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaService {

    private Set<Conta> contas = new HashSet<>();

    public Set<Conta> listarContasAbertas() {
        return contas;
    }

    private ConnectionFactory connection;
    public ContaService() {
        this.connection = new ConnectionFactory();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

//    public void abrir(DadosAberturaConta dadosDaConta) {
//        var cliente = new Cliente(dadosDaConta.dadosCliente());
//        var conta = new Conta(dadosDaConta.numero(), cliente);
//        if (contas.contains(conta)) {
//            throw new RegraDeNegocioException("Já existe outra conta aberta com o mesmo número!");
//        }

//        // contas.add(conta); //ao inves de adicionar o elemento em uma lista estatica, vamos INSERIR (insert) ao banco de dados
//
//        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" + "VALUES (?, ?, ?, ?, ?)";
//
//        Connection conn = connection.recuperarConexao();
//
//        //QUERY - Consulta
//        try {
//            PreparedStatement preparedStatement = conn.prepareStatement(sql);
//
//            preparedStatement.setInt(1, conta.getNumero());
//            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
//            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
//            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
//            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());
//
//            preparedStatement.execute();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void abrir(DadosAberturaConta dadosDaConta){
        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }

        conta.sacar(valor);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }

        conta.depositar(valor);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        contas.remove(conta);
    }

    private Conta buscarContaPorNumero(Integer numero) {
        return contas
                .stream()
                .filter(c -> c.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
    }
}
