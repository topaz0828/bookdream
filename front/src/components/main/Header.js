import React from 'react';

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMyPage = props.moveMyPage;
		this.contents = props.contents;
	}

	componentDidMount() {
		$('#searchContentsButton').on('click', () => {
			this.getList();
		});
		$('#searchContentsInput').keypress((event) => {
			if (event.which == 13) {
				this.getList();
			}
		});
	}

	getList() {
		var query = $('#searchContentsInput').val().trim();
		this.contents.getList(query);
	}

	render() {
		return (
			<div className='row'>
				<div className='col-sm-6 col-md-3' align='center'><h1>Marker</h1><h6><strong>Mark</strong> the moments of your life.</h6></div>
				<div className='col-sm-12 col-md-6' style={{paddingTop: '40px'}} align='center'>
					<div className="input-group" style={{maxWidth: '450px'}}>
						<input id='searchContentsInput' type="text" className="form-control" aria-describedby="sizing-addon2"/>
						<span className="input-group-btn">
							<button id='searchContentsButton' className="btn btn-default" type="button">
								<span className='glyphicon glyphicon-search' aria-hidden='true'></span>
							</button>
						</span>
					</div>
				</div>
				<div className='col-sm-6 col-md-3' style={{paddingTop: '35px', paddingRight: '70px'}} align='right'>
					<form method='POST' action='/api/user/logout'>
						<button type='button' className='btn btn-default' data-toggle='modal' data-target='#addContentsModal'>
							<span className='glyphicon glyphicon-plus' aria-hidden='true'></span>
						</button>
						&nbsp;&nbsp;
						<button type='button' className='btn btn-default' onClick={this.moveMyPage}>
							<span className='glyphicon glyphicon-user' aria-hidden='true'></span>
						</button>
						&nbsp;&nbsp;
						<button type='submit' className='btn btn-default'>
							<span className='glyphicon glyphicon-log-out' aria-hidden='true'></span>
						</button>
					</form>
				</div>
			</div>
		);
	}
}

export default Header;