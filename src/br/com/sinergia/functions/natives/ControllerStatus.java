package br.com.sinergia.functions.natives;

public enum ControllerStatus {
    Nenhum, Editando, Adicionando;

    public ControllerStatus Status;

    public ControllerStatus getStatus() {
        return Status;
    }

    public void setStatus(ControllerStatus newStatus) {
        this.Status = newStatus;
    }
}

