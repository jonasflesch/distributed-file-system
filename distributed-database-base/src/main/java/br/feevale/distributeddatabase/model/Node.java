package br.feevale.distributeddatabase.model;

/**
 * Created by jonasflesch on 6/15/15.
 */
public class Node {

	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Node node = (Node) o;

		return !(address != null ? !address.equals(node.address) : node.address != null);

	}

	@Override
	public int hashCode() {
		return address != null ? address.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Node{" +
				"address='" + address + '\'' +
				'}';
	}
}
