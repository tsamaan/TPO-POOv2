package notifiers;

import interfaces.INotifier;

public abstract class NotifierFactory {

    public abstract INotifier createEmailNotifier();

}
