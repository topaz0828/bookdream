import React from 'react';

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMyPage = props.moveMyPage;
		this.getList = props.getList;
	}

	componentDidMount() {
		this.searchContentsInput = $('#searchContentsInput');
		$('searchContentsButton').on('click', () => {
			this.getList(this.searchContents.val());
		});
	}

	render() {
		return (
			<div className='row'>
				<div className='col-sm-6 col-md-3 h1' align='center'>Bookdream</div>
				<div className='col-sm-12 col-md-6' style={{paddingTop: '25px'}} align='center'>
					<div className="input-group" style={{maxWidth: '450px'}}>
						<input id='searchContentsInput' type="text" className="form-control" aria-describedby="sizing-addon2"/>
						<span className="input-group-btn">
							<button id='searchContentsButton' className="btn btn-default" type="button" onClick={this.getList}>
								<span className='glyphicon glyphicon-search' aria-hidden='true'></span>
							</button>
						</span>
					</div>
				</div>
				<div className='col-sm-6 col-md-3' style={{paddingTop: '25px', paddingRight: '70px'}} align='right'>
					<button type='button' className='btn btn-info' data-toggle='modal' data-target='#addContentsModal'>
						<span className='glyphicon glyphicon-plus' aria-hidden='true'></span>
					</button>
					&nbsp;&nbsp;
					<button type='button' className='btn btn-info' onClick={this.moveMyPage}>
						<span className='glyphicon glyphicon-user' aria-hidden='true'></span>
					</button>
				</div>
			</div>
		);
	}
}

export default Header;