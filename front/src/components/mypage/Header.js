import React from 'react';

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMainView = props.moveMainView;
	}

	openAddModal() {
		$('#addContentsModal').modal({backdrop: 'static'});
	}

	render() {
		return (
			<div className='row' style={{maxWidth: '1300px'}}>
				<div className='col-sm-6 col-md-3' align='center'>
					<span onClick={this.moveMainView} style={{cursor: 'default'}}>
						<h1>Marker</h1><h6><strong>Mark</strong> the moments of your life.</h6>
					</span>
				</div>
				<div className='col-sm-12 col-md-6' style={{paddingTop: '40px'}} align='center'>
				</div>
				<div className='col-sm-6 col-md-3' style={{paddingTop: '35px', paddingRight: '70px'}} align='right'>
					<form method='POST' action='/api/user/logout'>
						<button type='submit' className='btn btn-default'>
							<span className='glyphicon glyphicon-log-out' aria-hidden='true'></span>
						</button>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<button type='button' className='btn btn-default' onClick={this.openAddModal}>
							<span className='glyphicon glyphicon-plus' aria-hidden='true'></span>
						</button>
						&nbsp;&nbsp;
						<button type='button' className='btn btn-default' onClick={this.moveMainView}>
							<span className='glyphicon glyphicon-menu-left' aria-hidden='true'></span>
						</button>
					</form>
				</div>
			</div>
		);
	}
}

export default Header;