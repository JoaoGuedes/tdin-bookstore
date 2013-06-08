# Estudantes
Ricardo Jorge de Sousa Teixeira - ei08040@fe.up.pt
João Nuno Santos Gusmão Guedes - ei07043@fe.up.pt

# Instruções

## Abrir o projeto principal no Netbeans - Bookstore

## Queues
	Criar a ConnectionFactory no Glassfish: jms/ConnectionFactory
	Criar a Queue no Glassfish: jms/myQueue

## Email
	Configurar a JavaMailSession no Glassfish mail/myMail
	
## Criar a base de dados Derby
	jdbc:derby://localhost:1527/BookstoreDB [root on ROOT]

## Build and deploy/run pela seguinte ordem:
	Common
	Store
	Warehouse
	StoreGUI
	WarehouseGUI
	Console
	WebStore
