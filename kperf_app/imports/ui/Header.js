import React from 'react'
import PropTypes from 'prop-types'
import { withRouter } from 'react-router-dom'
import { Meteor } from 'meteor/meteor'

import { Navbar, Nav, Form, FormControl, Button} from 'react-bootstrap'
import { Container, Row, Col } from 'react-bootstrap'


class Header extends React.Component {
  renderRightMenu() {
    // if(Meteor.userId()) {
    //   return (
    //     <Button onClick={ () => Meteor.logout(() => this.props.history.push('/login')) }>
    //       Log Out
    //     </Button>
    //   )
    // }
  }
  render() {
    return (
      <div>
      <Navbar bg="dark" variant="dark">
        <Navbar.Brand href="#home">KTest</Navbar.Brand>
          <Nav className="mr-auto">
            <Nav.Link href="/">Home</Nav.Link>
            <Nav.Link href="/docs">Project</Nav.Link>
          </Nav>
          <Form inline>
          <FormControl type="text" placeholder="Search" className="mr-sm-2" />
          <Button variant="outline-info">Search</Button>
          </Form>
        </Navbar>
      </div>
    )
  }
}

export default withRouter(Header)

Header.propTypes = {
  history: PropTypes.object.isRequired,
  children: PropTypes.node,
}
