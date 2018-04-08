import React from 'react';

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMainView = props.moveMainView;
	}

	render() {
		return (
			<div className='row'>
				<div className='col-sm-6 col-md-3' align='center'><h1>Marker</h1><h6><strong>Mark</strong> the moments of your life.</h6></div>
				<div className='col-sm-12 col-md-6' style={{paddingTop: '40px'}} align='center'>
				</div>
				<div className='col-sm-6 col-md-3' style={{paddingTop: '35px', paddingRight: '70px'}} align='right'>
					<button type='button' className='btn btn-info btn-lg' data-toggle='modal' data-target='#addContentsModal'>
						<span className='glyphicon glyphicon-plus' aria-hidden='true'></span>
					</button>
					&nbsp;&nbsp;
					<button type='button' className='btn btn-info btn-lg' onClick={this.moveMainView}>
						<span className='glyphicon glyphicon-menu-left' aria-hidden='true'></span>
					</button>
				</div>
			</div>
		);
	}
}

export default Header;