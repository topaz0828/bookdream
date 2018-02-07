import React from 'react';

class Header extends React.Component {
	constructor(props) {
		super(props);
		this.moveMainView = props.moveMainView;
	}

	render() {
		return (
			<div>
				<button type='button' className='btn btn-info' onClick={this.moveMainView}>
					<span className='glyphicon glyphicon-menu-left' aria-hidden='true'></span>
				</button>
			</div>
		);
	}
}

export default Header;