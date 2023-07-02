console.log('activo');

async function getProducts(){
	const res = await fetch('/products?products=all');
	const json = await res.json();
	renderProducts(json);
}

function renderProducts(json){
	const cardContainer = document.getElementById("cardContainer");
	cardContainer.innerHTML = "";
	json.forEach((product,index)=>{
		const card = createCard(product);
		cardContainer.appendChild(card);
	})
}

function createCard({id,descripcion, nombre, precio}){
	const card = createElement("div","col-10 col-sm-8 col-md-5 col-lg-3 card");
	const cardBody = createElement('div',"card-body d-flex flex-column justify-content-between");
	card.appendChild(cardBody);
	const cardTitle = createElement("h5","card-title");
	cardTitle.innerText = nombre;
	cardBody.appendChild(cardTitle);
	const description = createElement('p', "card-text");
	description.innerText= descripcion;
	cardBody.appendChild(description);
	const buttonEdit= createElement('button',"me-3 btn btn-primary");
	const buttonContainer = document.createElement('div',"d-flex flex-row justify-content-center");
	buttonEdit.dataset.bsToggle="modal";
	buttonEdit.dataset.bsTarget="#exampleModal";
	buttonEdit.innerText = "Editar";
	buttonEdit.addEventListener('click',()=>openModal({id, descripcion, nombre, precio}));
	const buttonDelete= createElement('button',"btn ms-3 btn-danger");
	buttonDelete.innerText="Borrar";
	buttonDelete.addEventListener('click',()=>deleteProduct(id));
	buttonContainer.appendChild(buttonEdit);
	buttonContainer.appendChild(buttonDelete);
	const price = createElement('h5', "card-price");
	price.innerText = `${precio}$`;
	cardBody.appendChild(price);
	cardBody.appendChild(buttonContainer);
	return card;
}

function openModal({id, nombre,descripcion,precio }){
	const exampleModal = document.getElementById('exampleModal');
	const product = exampleModal.querySelector('#producto');
  	const desc = exampleModal.querySelector('#descripcion');
 	const productId = exampleModal.querySelector('#productid');
	const methodName = exampleModal.querySelector('#methodname');
	exampleModal.querySelector('#precio').value = precio;
	methodName.value = 'UPDATE'
 	product.value = nombre;
 	productId.value=id;
 	desc.value=descripcion;
}

async function updateProduct(){
	const form = document.getElementById('modal-form');
	const data = new URLSearchParams(new FormData(form));
	await sendData('/products',{
		body:data,
		method:'post'
	})
	const modal = bootstrap.Modal.getInstance(document.querySelector("#exampleModal"));
	modal.hide();
}

async function deleteProduct(id){
	const res = await sendData('/products',{
		method: 'post',
		body:new URLSearchParams([["methodname","DELETE"],["id",id]])
	});
}

const exampleModal = document.getElementById('exampleModal');
exampleModal.addEventListener('hidden.bs.modal',cleanModal);

function cleanModal(){
	[...exampleModal.querySelector('form').elements].forEach(element=>{
		console.log(element);
		element.value = ""
	})
	exampleModal.querySelector('form').querySelector('input#methodname').value = "INSERT";
	console.log(exampleModal.querySelector('form').elements)
}

function createElement(name, className){
	const element = document.createElement(name);
	element.className = className;
	return element;
}

async function sendData(url, config){
	const resp = await fetch(url, config);
	if(resp.ok){
		const toast = document.getElementById('liveToast');
		getProducts();
		const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toast).show();
	}
}

