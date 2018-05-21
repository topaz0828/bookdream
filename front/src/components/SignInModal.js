import React from 'react';

class SignInModal extends React.Component {
    componentDidMount() {
        this.modal = $('#signInModal');
    }

    show() {
        this.modal.modal();
    }

    signInFacebook() {
        $.get("/api/signin/facebook/oauth-uri", function(data) {
            document.location = data;
        });
    }

    signInGoogle() {
        $.get("/api/signin/google/oauth-uri", function(data) {
            document.location = data;
        });
    }

    render() {
        return (
            <div className="modal fade" id="signInModal" tabIndex="-1" role="dialog" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header" style={{textAlign: 'center'}}>
                            <h1>Marker</h1>
                            <h5>Mark the moments of your life.</h5>
                        </div>
                        <div className="modal-body" style={{textAlign: 'center'}}>
                            <button type="button" className='facebook-login-button' onClick={this.signInFacebook}>facebook</button>
                            <div style={{height: '10px'}}></div>
                            <button className='google-login-button' onClick={this.signInGoogle} style={{width: '200px'}}>
                                <font color='#008DE8'>G</font>
                                <font color='#FF073B'>o</font>
                                <font color='#FFB54E'>o</font>
                                <font color='#008DE8'>g</font>
                                <font color='#00AB64'>l</font>
                                <font color='#FF073B'>e</font>
                            </button>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">닫기</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default SignInModal;